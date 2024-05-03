package ru.chernyukai.projects.dating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chernyukai.projects.dating.model.Interest;
import ru.chernyukai.projects.dating.model.Profile;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    void deleteByProfile(Profile profile);

}
