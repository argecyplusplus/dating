package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chernyukai.projects.dating.exceptions.InvalidProfileException;
import ru.chernyukai.projects.dating.model.*;
import ru.chernyukai.projects.dating.repository.InterestRepository;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.repository.PhotoRepository;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    InterestRepository interestRepository;

    @Autowired
    PhotoRepository photoRepository;



    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean userIsAdmin (){
        User user = getCurrentUser();
        return user.getAuthorities().stream()
                .filter(authority -> authority instanceof UserAuthority)
                .map(authority -> (UserAuthority) authority)
                .anyMatch(UserAuthority.ADMIN::equals);
    }

    private boolean checkAccessToProfile(Profile profile) {
        User user = getCurrentUser();

        //Если зашел админ
        if (userIsAdmin()){
            return true;
        }

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        //Если не заполнен свой профиль
        if (myProfileOptional.isEmpty()){
            System.out.println("Не заполнен профиль");
            return false;
        }
        Profile myProfile = myProfileOptional.get();
        //Если видишь свой профиль
        if(Objects.equals(profile.getId(), myProfile.getId())){
            System.out.println("Это твой профиль");
            return false;
        }

        //Твой профиль отключен
        if(!myProfile.isVisible()){
            System.out.println("Твой профиль отключен");
            return false;
        }

        //Проверка на пол
        if (profile.getGender().equals(myProfile.getGender())){
            System.out.println("ЛГБТ ФУ");
            return false;
        }

        //Проверка на город
        if (!profile.getCity().equals(myProfile.getCity())){
            System.out.println("Не тот город");
            return false;
        }

        return true;
    }

    @Transactional
    public List<Interest> updateInterests(Long profileId, List<String> interestTexts) {
        // Получение профиля по его идентификатору
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);

        List<Interest> newInterests = new ArrayList<>();

        if (optionalProfile.isPresent() && interestTexts != null && !interestTexts.isEmpty()) {
            Profile profile = optionalProfile.get();

            //Удаляем старые интересы
            interestRepository.deleteByProfile(profile);

            // Создание новых объектов Interest из текстовых описаний и добавление их в список
            for (String text : interestTexts) {
                Interest interest = new Interest();
                interest.setValue(InterestValue.getByTitle(text));
                interest.setProfile(profile);
                interestRepository.save(interest);
                newInterests.add(interest);
            }
        }
        return newInterests;
    }

    @Transactional
    public List<ProfilePhoto> updatePhotos(Long profileId, List<String> photoLinks) {
        // Получение профиля по его идентификатору
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);

        List<ProfilePhoto> newPhotos = new ArrayList<>();

        if (optionalProfile.isPresent() && photoLinks != null && !photoLinks.isEmpty()) {
            Profile profile = optionalProfile.get();

            //Удаляем старые фото
            photoRepository.deleteByProfile(profile);

            // Создание новых объектов ProfilePhoto из текстовых ссылок и добавление их в список
            for (String link : photoLinks) {
                ProfilePhoto photo = new ProfilePhoto();
                photo.setLink(link);
                photo.setProfile(profile);
                photoRepository.save(photo);
                newPhotos.add(photo);

            }
        }
        return newPhotos;
    }


    // Посчет общих интересов
    private static int countCommonInterests(Profile profile1, Profile profile2) {
        Set<InterestValue> interests1 = profile1.getInterests().stream()
                .map(Interest::getValue)
                .collect(Collectors.toSet());

        Set<InterestValue> interests2 = profile2.getInterests().stream()
                .map(Interest::getValue)
                .collect(Collectors.toSet());

        // Находим общие интересы
        interests1.retainAll(interests2);

        return interests1.size();
    }


    @Override
    public Page<ProfileInfo> getAllProfiles(int page, int minAge, int maxAge) throws AccessDeniedException {
        List<Profile> allProfiles = profileRepository.findProfilesBy();

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(getCurrentUser());
        if (myProfileOptional.isEmpty()){
            throw new AccessDeniedException("Вам нужно создать профиль");
        }

        Profile myProfile = myProfileOptional.get();
        List<Profile> allowedProfiles = new ArrayList<>();

        //Получение доступных профилей
        for (Profile profile: allProfiles){
            if (checkAccessToProfile(profile)){

                Optional<Match> potentialPair = matchRepository.getPairByProfile1AndProfile2(myProfile.getId(), profile.getId());

                if (potentialPair.isEmpty()){
                    if (minAge <= profile.getAge() && profile.getAge()<=maxAge)
                    {

                        allowedProfiles.add(profile);
                    }
                }
            }
        }

        // Сортируем профили по убыванию количества общих интересов
        allowedProfiles.sort((p1, p2) -> {
            int commonEdges1 = countCommonInterests(p1, myProfile);
            int commonEdges2 = countCommonInterests(p2, myProfile);
            return Integer.compare(commonEdges2, commonEdges1); // Сортировка по убыванию
        });


        //Создание списка ProfileInfo из списка Profile
        List<ProfileInfo> allowedProfileInfos  = new ArrayList<>();
        for (Profile profile: allowedProfiles){
            allowedProfileInfos.add(new ProfileInfo(
                    profile.getId(),
                    profile.getName(),
                    profile.getAge(),
                    profile.getPhotos().stream()
                            .map(ProfilePhoto::getLink)
                            .collect(Collectors.toList()),
                    profile.getCity(),
                    profile.getGender(),
                    profile.getInterests().stream()
                            .map(interest -> {
                                InterestValue value = interest.getValue();
                                return value != null ? value.getTitle() : null;
                            })
                            .collect(Collectors.toList()),
                    profile.getDescription(),
                    null
            ));
        }



        //Вывести страницу отфильтрованных
        int start = Math.min(page * 10, allowedProfileInfos.size());
        int end = Math.min((page + 1) * 10, allowedProfileInfos.size());
        List<ProfileInfo> profilesOnPage = allowedProfileInfos.subList(start, end);
        Pageable pageable = PageRequest.of(page, 10);
        return new PageImpl<>(profilesOnPage, pageable, allowedProfileInfos.size());
    }

    @Override
    public Optional<ProfileInfo> getProfileById(Long id) {
        Optional<Profile> profileOptional = profileRepository.getProfileById(id);
        if (profileOptional.isPresent()){
            Profile profile = profileOptional.get();
            if (checkAccessToProfile(profile)){
                return Optional.of(new ProfileInfo(
                        profile.getId(),
                        profile.getName(),
                        profile.getAge(),
                        profile.getPhotos().stream()
                                .map(ProfilePhoto::getLink)
                                .collect(Collectors.toList()),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getInterests().stream()
                                .map(interest -> {
                                    InterestValue value = interest.getValue();
                                    return value != null ? value.getTitle() : null;
                                })
                                .collect(Collectors.toList()),
                        profile.getDescription(),
                        null
                ));
            }
            else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public ProfileInfo editProfileById(Long id, ProfileInfo editedProfile) throws AccessDeniedException {
        Profile profile = profileRepository.getProfileById(id).get();

        List <ProfilePhoto> newPhotos;
        List <Interest> newInterests;

        if (userIsAdmin()){
            //Заменить анкету
            profile.setName(editedProfile.getName());
            profile.setAge(editedProfile.getAge());
            newPhotos = updatePhotos(profile.getId(), editedProfile.getPhotos());
            profile.setCity(editedProfile.getCity());
            profile.setGender(editedProfile.getGender());
            newInterests = updateInterests(profile.getId(), editedProfile.getInterests());
            profile.setDescription(editedProfile.getDescription());
            profile.setSocialLink(editedProfile.getSocialLink());
            profileRepository.save(profile);
            return new ProfileInfo(
                    profile.getId(),
                    profile.getName(),
                    profile.getAge(),
                    newPhotos.stream()
                            .map(ProfilePhoto::getLink)
                            .collect(Collectors.toList()),
                    profile.getCity(),
                    profile.getGender(),
                    newInterests.stream()
                            .map(interest -> {
                                InterestValue value = interest.getValue();
                                return value != null ? value.getTitle() : null;
                            })
                            .collect(Collectors.toList()),
                    profile.getDescription(),
                    profile.getSocialLink()
            );
        }
        else {
            throw new AccessDeniedException("Доступ запрещен!");
        }
    }

    @Override
    public void deleteProfileById(Long id) throws AccessDeniedException{
        Profile profile = profileRepository.getProfileById(id).get();
        if (userIsAdmin()){
            //Удалить анкету
            profileRepository.delete(profile);
        }
        else {
            throw new AccessDeniedException("Доступ запрещен!");
        }
    }

    //MY PROFILE

    @Override
    public Optional<ProfileInfo> getMyProfile(){
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        if (myProfileOptional.isPresent()){
            Profile myProfile =  myProfileOptional.get();
            return Optional.of(new ProfileInfo(
                    myProfile.getId(),
                    myProfile.getName(),
                    myProfile.getAge(),
                    myProfile.getPhotos().stream()
                            .map(ProfilePhoto::getLink)
                            .collect(Collectors.toList()),
                    myProfile.getCity(),
                    myProfile.getGender(),
                    myProfile.getInterests().stream()
                            .map(interest -> {
                                InterestValue value = interest.getValue();
                                return value != null ? value.getTitle() : null;
                            })
                            .collect(Collectors.toList()),
                    myProfile.getDescription(),
                    myProfile.getSocialLink()
            ));
        }
        else{
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public ProfileInfo editOrCreateMyProfile(ProfileInfo newProfile) throws InvalidProfileException {
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        Profile profile;

        List <ProfilePhoto> newPhotos;
        List <Interest> newInterests;

        //Проверка валидности
        int age = newProfile.getAge();
        if (!(18<= age && age <=100)){
            throw new InvalidProfileException("Ошибка в указании возраста");
        }
        String gender = newProfile.getGender();
        if (!(gender.equals("Мужской") || gender.equals("Женский"))){
            throw new InvalidProfileException("Ошибка в указании пола");
        }


        if (myProfileOptional.isPresent()){
            profile = myProfileOptional.get();
            //Заменить анкету
            profile.setName(newProfile.getName());
            profile.setAge(age);
            newPhotos = updatePhotos(profile.getId(), newProfile.getPhotos());
            profile.setCity(newProfile.getCity());
            profile.setGender(gender);
            newInterests = updateInterests(profile.getId(), newProfile.getInterests());
            profile.setDescription(newProfile.getDescription());
            profile.setSocialLink(newProfile.getSocialLink());
            profileRepository.save(profile);
        }
        else{
            //Создать новую
            profile = new Profile(
                    null,
                    newProfile.getName(),
                    age,
                    null,
                    newProfile.getCity(),
                    gender,
                    null,
                    newProfile.getDescription(),
                    newProfile.getSocialLink(),
                    user,
                    true
            );
            newPhotos = updatePhotos(profile.getId(), newProfile.getPhotos());
            newInterests = updateInterests(profile.getId(), newProfile.getInterests());

            profileRepository.save(profile);
        }
        return new ProfileInfo(
                profile.getId(),
                profile.getName(),
                profile.getAge(),
                newPhotos.stream()
                        .map(ProfilePhoto::getLink)
                        .collect(Collectors.toList()),
                profile.getCity(),
                profile.getGender(),
                newInterests.stream()
                        .map(interest -> {
                            InterestValue value = interest.getValue();
                            return value != null ? value.getTitle() : null;
                        })
                        .collect(Collectors.toList()),
                profile.getDescription(),
                profile.getSocialLink()
        );
    }

    @Override
    public void deleteMyProfile(){
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        myProfileOptional.ifPresent(profile -> profileRepository.delete(profile));
    }

}
