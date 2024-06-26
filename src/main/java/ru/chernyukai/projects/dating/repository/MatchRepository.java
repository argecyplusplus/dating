package ru.chernyukai.projects.dating.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.chernyukai.projects.dating.model.Match;
import ru.chernyukai.projects.dating.model.Profile;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {
        //get pairs with user
    @Query(value="select * from matches where is_pair=true and (profile1_id= :profileId or profile2_id= :profileId)", nativeQuery = true)
    List<Match> getAllPairsByProfileId(@Param("profileId") Long profileId);

    //get received matches
    @Query(value="select * from matches where is_pair=false and (profile2_id= :profileId)", nativeQuery = true)
    List<Match> getAllMatchesByProfileId(@Param("profileId") Long profileId);

    Optional<Match> getMatchByProfile1AndProfile2(Profile profile1, Profile profile2);

    @Query(value = "select * from matches where is_pair=true and (profile1_id = :profile1 and profile2_id = :profile2 or  profile1_id = :profile2 and profile2_id = :profile1  ) limit 1", nativeQuery = true)
    Optional<Match> getPairByProfile1AndProfile2 (@Param("profile1") Long profile1Id, @Param("profile2") Long profile2Id);

    Optional<Match> getMatchById(Long id);
}
