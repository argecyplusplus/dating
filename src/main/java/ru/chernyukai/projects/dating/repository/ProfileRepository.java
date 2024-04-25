package ru.chernyukai.projects.dating.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.chernyukai.projects.dating.model.Profile;

import java.util.List;
import java.util.Optional;


public interface ProfileRepository extends JpaRepository<Profile, Long>, PagingAndSortingRepository<Profile, Long>{
    //get all profiles (paging)
    List<Profile> getProfilesBy();

    //get random profile
    @Query(value = "select * from profiles orger by random() limit 1", nativeQuery = true)
    Profile getRandomProfile ();

    //get profile by id
    Optional<Profile> getProfileById(Long id);
}
