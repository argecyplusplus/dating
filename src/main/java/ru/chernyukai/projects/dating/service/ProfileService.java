package ru.chernyukai.projects.dating.service;

import org.springframework.security.access.AccessDeniedException;
import ru.chernyukai.projects.dating.model.Profile;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    Page<Profile> getAllProfiles(int page);

    Optional<Profile> getProfileById(Long id);

    Profile editProfileById(Long id, Profile editedProfile) throws AccessDeniedException;

    void deleteProfileById(Long id);

    //MY PROFILE
    Optional<Profile> getMyProfile();

    Profile editOrCreateMyProfile(Profile newProfile);

    void deleteMyProfile();
}
