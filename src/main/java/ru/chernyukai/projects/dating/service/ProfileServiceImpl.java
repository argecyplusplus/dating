package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    ProfileRepository profileRepository;


    @Override
    public List<Profile> getAllProfiles(int page) {
        List<Profile> allProfiles = profileRepository.getProfilesBy(PageRequest.of(page, 10));
        List<Profile> allowedProfiles = new ArrayList<>();
        Profile myProfile = profileRepository.getProfileById(1L).get(); // тут нужен метод для получения МОЕЙ АНКЕТЫ
        int minAge = 18, maxAge = 100;
        if(myProfile.getMinAge() != 0){
            minAge = myProfile.getAge()-5;
        }
        if(myProfile.getMaxAge() != 0){
            maxAge = myProfile.getAge()+5;
        }

        for (Profile profile: allProfiles){
            //
            if(profile != myProfile && (
                !profile.getGender().equals(myProfile.getGender())
                &&
                minAge <= profile.getAge() && profile.getAge()<= maxAge
                )
            )
            {
                allProfiles.add(profile);
            }
        }

        return allowedProfiles;
    }

    @Override
    public Optional<Profile> getProfileById() {
        return Optional.empty();
    }

    @Override
    public Profile createMyProfile() {
        return null;
    }

    @Override
    public Profile editMyProfile() {
        return null;
    }
}
