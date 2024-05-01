package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.chernyukai.projects.dating.model.Match;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.model.User;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService{
    @Autowired
    MatchRepository matchRepository;

    @Autowired
    ProfileRepository profileRepository;

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public void sendLike (Long id){
        matchRepository.save(
            new Match(
                    null,
                    false,
                    profileRepository.getProfileByUser(getCurrentUser()).get(),
                    profileRepository.getProfileById(id).get()
            )
        );
    }

    @Override
    public List<ProfileInfo> getMyMatches (){
        List <ProfileInfo> myMatchesProfiles = new ArrayList<>();

        User user = getCurrentUser();

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        if (myProfileOptional.isPresent()){
            Profile myProfile = myProfileOptional.get();

            List <Match> myMatches = matchRepository.getAllMatchesByProfileId(myProfile.getId());

            for (Match match: myMatches){
                Profile profile = match.getProfile1();
                myMatchesProfiles.add(new ProfileInfo(
                        profile.getId(),
                        profile.getName(),
                        profile.getAge(),
                        profile.getAvatar(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getDescription(),
                        null
                ));

            }
        }
        return myMatchesProfiles;
    }

    @Override
    public List<ProfileInfo> getMyPairs (){
        List <ProfileInfo> myPairsProfiles = new ArrayList<>();

        User user = getCurrentUser();

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);
        if (myProfileOptional.isPresent()){
            Profile myProfile = myProfileOptional.get();

            List <Match> myPairs = matchRepository.getAllPairsByProfileId(myProfile.getId());

            for (Match pair: myPairs){
                Profile profile;
                if (pair.getProfile1().equals(myProfile)){
                    profile = pair.getProfile2();
                }
                else{
                    profile = pair.getProfile1();
                }
                myPairsProfiles.add(new ProfileInfo(
                        profile.getId(),
                        profile.getName(),
                        profile.getAge(),
                        profile.getAvatar(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getDescription(),
                        profile.getSocialLink()
                ));

            }
        }
        return myPairsProfiles;

    }
    

}
