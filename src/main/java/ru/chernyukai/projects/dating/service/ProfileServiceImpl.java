package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.chernyukai.projects.dating.model.Profile;
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


        Optional<Profile> myProfileOptional = profileRepository.getProfileById(user.getId());


        //Если не заполнен свой профиль
        if (myProfileOptional.isEmpty()){
            return false;
        }
        Profile myProfile = myProfileOptional.get();
        //Если видишь свой профиль
        if(Objects.equals(profile.getId(), myProfile.getId())){
            return false;
        }

        //Твой профиль отключен
        if(!myProfile.isVisible()){
            return false;
        }

        //Проверка на пол
        if (profile.getGender().equals(myProfile.getGender())){
            return false;
        }

        //Проверка на город
        if (!profile.getCity().equals(myProfile.getCity())){
            return false;
        }

        //Подумать тут про увлечения

        return true;
    }


    @Override
    public Page<Profile> getAllProfiles(int page) {
        List<Profile> allProfiles = profileRepository.getProfilesBy();
        List<Profile> allowedProfiles = new ArrayList<>();

        for (Profile profile: allProfiles){
            if (checkAccessToProfile(profile)){
                allowedProfiles.add(profile);
            }
        }

        //Вывести страницу отфильтрованных
        int start = Math.min(page * 10, allowedProfiles.size());
        int end = Math.min((page + 1) * 10, allowedProfiles.size());
        List<Profile> profilesOnPage = allowedProfiles.subList(start, end);
        Pageable pageable = PageRequest.of(page, 10);
        return new PageImpl<>(profilesOnPage, pageable, allowedProfiles.size());
    }

    @Override
    public Optional<Profile> getProfileById(Long id) {
        Optional<Profile> profileOptional = profileRepository.getProfileById(id);
        if (profileOptional.isPresent()){
            Profile profile = profileOptional.get();
            if (checkAccessToProfile(profile)){
                return Optional.of(profile);
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
    public Profile editProfileById(Long id, Profile editedProfile) throws AccessDeniedException {
        Profile profile = profileRepository.getProfileById(id).get();
        if (userIsAdmin()){
            //Заменить анкету
            Long oldId = profile.getId();
            profileRepository.delete(profile);
            editedProfile.setId(oldId);
            profileRepository.save(editedProfile);

            return profile;
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
    public Optional<Profile> getMyProfile(){
        User user = getCurrentUser();
        return profileRepository.getProfileByUser(user);
    }

    @Override
    public Profile editOrCreateMyProfile(Profile newProfile) {
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        if (myProfileOptional.isPresent()){
            Profile profile = myProfileOptional.get();
            //Заменить анкету
            Long oldId = profile.getId();
            profileRepository.delete(profile);
            newProfile.setId(oldId);
            profileRepository.save(newProfile);
        }
        else{
            //Создать новую
            profileRepository.save(new Profile(
                    null,
                    newProfile.getName(),
                    newProfile.getAge(),
                    newProfile.getAvatar(),
                    newProfile.getCity(),
                    newProfile.getGender(),
                    newProfile.getDescription(),
                    newProfile.getSocialLink(),
                    newProfile.getMinAge(),
                    newProfile.getMaxAge(),
                    user,
                    newProfile.isVisible()
            ));
        }
        return newProfile;
    }

    @Override
    public void deleteMyProfile(){
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        myProfileOptional.ifPresent(profile -> profileRepository.delete(profile));
    }

}
