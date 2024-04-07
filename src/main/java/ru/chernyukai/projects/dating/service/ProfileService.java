package ru.chernyukai.projects.dating.service;

import ru.chernyukai.projects.dating.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    List<Profile> getAllProfiles(int page);

    Optional<Profile> getProfileById();

    Profile createMyProfile();

    Profile editMyProfile();


}
