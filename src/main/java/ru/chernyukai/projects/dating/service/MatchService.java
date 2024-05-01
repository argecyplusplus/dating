package ru.chernyukai.projects.dating.service;

import ru.chernyukai.projects.dating.model.ProfileInfo;

import java.util.List;
import java.util.Optional;

public interface MatchService {
    void sendLike (Long id);

    List<ProfileInfo> getMyMatches ();

    List<ProfileInfo> getMyPairs ();

    Optional<ProfileInfo> getProfileFromMatch (Long matchId);

    Optional<ProfileInfo> getProfileFromPair (Long matchId);

    void createPair(Long matchId);

    void deleteMatch(Long matchId);
}
