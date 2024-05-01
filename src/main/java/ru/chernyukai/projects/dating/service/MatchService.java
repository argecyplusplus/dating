package ru.chernyukai.projects.dating.service;

import ru.chernyukai.projects.dating.model.ProfileInfo;

import java.util.List;

public interface MatchService {
    void sendLike (Long id);

    List<ProfileInfo> getMyMatches ();

    List<ProfileInfo> getMyPairs ();
}
