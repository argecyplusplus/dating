package ru.chernyukai.projects.dating.service;

import org.springframework.security.access.AccessDeniedException;
import ru.chernyukai.projects.dating.model.Profile;
import org.springframework.data.domain.Page;
import ru.chernyukai.projects.dating.model.ProfileInfo;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    boolean checkAccessToProfile(Profile profile);

    Page<ProfileInfo> getAllProfiles(int page, int minAge, int maxAge);

    Optional<ProfileInfo> getProfileById(Long id);

    ProfileInfo editProfileById(Long id, ProfileInfo editedProfile) throws AccessDeniedException;

    void deleteProfileById(Long id);

    //MY PROFILE
    Optional<ProfileInfo> getMyProfile();

    ProfileInfo editOrCreateMyProfile(ProfileInfo newProfile);

    void deleteMyProfile();

}
