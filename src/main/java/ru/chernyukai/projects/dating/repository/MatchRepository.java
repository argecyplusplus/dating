package ru.chernyukai.projects.dating.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.chernyukai.projects.dating.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {
    //get all matches (paging)
    List<Match> getMatchesBy(PageRequest pageRequest);

    //get pairs with user
    @Query(value="select * from matches where isPair=true and (person1= :profileId or person2= :profileId)", nativeQuery = true)
    List<Match> getAllPairsByProfileId(@Param("profileId") Long profileId);

    //get received matches
    @Query(value="select * from matches where isPair=false and (person2= :profileId)", nativeQuery = true)
    List<Match> getAllMatchesByProfileId(@Param("profileId") Long profileId);

    Optional<Match> getMatchById(Long id);
}
