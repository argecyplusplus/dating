package ru.chernyukai.projects.dating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chernyukai.projects.dating.model.ProfilePhoto;
import ru.chernyukai.projects.dating.model.Profile;

public interface PhotoRepository extends JpaRepository<ProfilePhoto, Long> {

    void deleteByProfile(Profile profile);

}