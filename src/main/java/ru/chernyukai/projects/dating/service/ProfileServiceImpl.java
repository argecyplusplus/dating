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
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.model.User;
import ru.chernyukai.projects.dating.model.UserAuthority;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    ProfileRepository profileRepository;

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

    private boolean checkAccessToProfile(Profile profile){
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

        //Подумать тут про увлечения

        return true;
    }


    @Override
    public Page<ProfileInfo> getAllProfiles(int page) {
        List<Profile> allProfiles = profileRepository.findProfilesBy();

        List<ProfileInfo> allowedProfiles = new ArrayList<>();

        for (Profile profile: allProfiles){
            if (checkAccessToProfile(profile)){
                allowedProfiles.add(new ProfileInfo(
                        profile.getName(),
                        profile.getAge(),
                        profile.getAvatar(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getDescription(),
                        profile.getSocialLink()
                ));
            }
        }

        //Вывести страницу отфильтрованных
        int start = Math.min(page * 10, allowedProfiles.size());
        int end = Math.min((page + 1) * 10, allowedProfiles.size());
        List<ProfileInfo> profilesOnPage = allowedProfiles.subList(start, end);
        Pageable pageable = PageRequest.of(page, 10);
        return new PageImpl<>(profilesOnPage, pageable, allowedProfiles.size());
    }

    @Override
    public Optional<ProfileInfo> getProfileById(Long id) {
        Optional<Profile> profileOptional = profileRepository.getProfileById(id);
        if (profileOptional.isPresent()){
            Profile profile = profileOptional.get();
            if (checkAccessToProfile(profile)){
                return Optional.of(new ProfileInfo(
                        profile.getName(),
                        profile.getAge(),
                        profile.getAvatar(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getDescription(),
                        profile.getSocialLink()
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

    @Override
    public ProfileInfo editProfileById(Long id, ProfileInfo editedProfile) throws AccessDeniedException {
        Profile profile = profileRepository.getProfileById(id).get();
        if (userIsAdmin()){
            //Заменить анкету
            profile.setName(editedProfile.getName());
            profile.setAge(editedProfile.getAge());
            profile.setAvatar(editedProfile.getAvatar());
            profile.setCity(editedProfile.getCity());
            profile.setGender(editedProfile.getGender());
            profile.setDescription(editedProfile.getDescription());
            profile.setSocialLink(editedProfile.getSocialLink());

            return new ProfileInfo(
                    profile.getName(),
                    profile.getAge(),
                    profile.getAvatar(),
                    profile.getCity(),
                    profile.getGender(),
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
                    myProfile.getName(),
                    myProfile.getAge(),
                    myProfile.getAvatar(),
                    myProfile.getCity(),
                    myProfile.getGender(),
                    myProfile.getDescription(),
                    myProfile.getSocialLink()
            ));
        }
        else{
            return Optional.empty();
        }
    }

    @Override
    public ProfileInfo editOrCreateMyProfile(ProfileInfo newProfile) {
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        Profile profile;

        if (myProfileOptional.isPresent()){
            profile = myProfileOptional.get();
            //Заменить анкету
            profile.setName(newProfile.getName());
            profile.setAge(newProfile.getAge());
            profile.setAvatar(newProfile.getAvatar());
            profile.setCity(newProfile.getCity());
            profile.setGender(newProfile.getGender());
            profile.setDescription(newProfile.getDescription());
            profile.setSocialLink(newProfile.getSocialLink());
            profileRepository.save(profile);
        }
        else{
            //Создать новую
            profile = new Profile(
                    null,
                    newProfile.getName(),
                    newProfile.getAge(),
                    newProfile.getAvatar(),
                    newProfile.getCity(),
                    newProfile.getGender(),
                    newProfile.getDescription(),
                    newProfile.getSocialLink(),
                    18,
                    100,
                    user,
                    true
            );


            profileRepository.save(profile);
        }
        return new ProfileInfo(
                profile.getName(),
                profile.getAge(),
                profile.getAvatar(),
                profile.getCity(),
                profile.getGender(),
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
