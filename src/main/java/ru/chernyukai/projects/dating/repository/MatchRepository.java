package ru.chernyukai.projects.dating.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.chernyukai.projects.dating.model.Match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long>, PagingAndSortingRepository<Match, Long> {
    //get all matches (paging)
    List<Match> getMatchesBy(PageRequest pageRequest);

    //get all matches by profile
    @Query(value="select * from matches where isPair=true and (person1=id or person2=id)", nativeQuery = true)
    List<Match> getAllPairsByProfileId(Long id);

    @Query(value="select * from matches where isPair=false and (person2=id)", nativeQuery = true)
    List<Match> getAllMatchesByProfileId(Long id);

    Optional<Match> getMatchById(Long id);
}
