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

    private boolean checkAccessToMatch (Long matchId){
        User user = getCurrentUser();

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        if (myProfileOptional.isEmpty()){
            return false;
        }

        Profile myProfile = myProfileOptional.get();

        Match match = matchRepository.getMatchById(matchId).get();

        Profile profile1 = match.getProfile1();
        Profile profile2 = match.getProfile2();

        return myProfile.getId().equals(profile1.getId())
                || myProfile.getId().equals(profile2.getId());

    }

    @Override
    public void sendLike (Long id){

        Profile sender = profileRepository.getProfileByUser(getCurrentUser()).get();
        Profile receiver = profileRepository.getProfileById(id).get();

        if (
                matchRepository.getMatchByProfile1AndProfile2(sender, receiver).isPresent()
        ){
            return;
        }

        if (
                matchRepository.getMatchByProfile1AndProfile2(receiver, sender).isPresent()
        ){
            Match match = matchRepository.getMatchByProfile1AndProfile2(receiver, sender).get();
            if (!match.isPair()){
                match.setPair(true);
            }
            matchRepository.save(match);
            return;
        }

        matchRepository.save(
                new Match(
                        null,
                        false,
                        sender,
                        receiver
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
                        match.getId(),
                        profile.getName(),
                        profile.getAge(),
                        profile.getPhotos(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getInterests(),
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
                        profile.getPhotos(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getInterests(),
                        profile.getDescription(),
                        profile.getSocialLink()
                ));

            }
        }
        return myPairsProfiles;

    }

    @Override
    public Optional<ProfileInfo> getProfileFromMatch (Long matchId){
        Optional<Match> matchOptional = matchRepository.getMatchById(matchId);
        if (matchOptional.isPresent() && checkAccessToMatch(matchId)){
            Match match = matchOptional.get();
            Profile profile = match.getProfile1();

            return Optional.of (
                    new ProfileInfo(
                            match.getId(),
                            profile.getName(),
                            profile.getAge(),
                            profile.getPhotos(),
                            profile.getCity(),
                            profile.getGender(),
                            profile.getInterests(),
                            profile.getDescription(),
                            null

                    )

            );
        }
        else{
            return Optional.empty();
        }
    }


    @Override
    public Optional<ProfileInfo> getProfileFromPair (Long matchId){
        Optional<Match> matchOptional = matchRepository.getMatchById(matchId);
        if (matchOptional.isPresent() && checkAccessToMatch(matchId)){
            Match match = matchOptional.get();
            Profile profile;

            User user = getCurrentUser();
            Profile myProfile = profileRepository.getProfileByUser(user).get();


            if (match.getProfile1().getId().equals(myProfile.getId())){
                profile = match.getProfile2();
            }
            else{
                profile = match.getProfile1();
            }

            return Optional.of(
                    new ProfileInfo(
                            match.getId(),
                            profile.getName(),
                            profile.getAge(),
                            profile.getPhotos(),
                            profile.getCity(),
                            profile.getGender(),
                            profile.getInterests(),
                            profile.getDescription(),
                            profile.getSocialLink()

                    )
            );

        }
        else{
            return Optional.empty();
        }

    }

    @Override
    public void createPair(Long matchId) {
        Match match = matchRepository.getMatchById(matchId).get();
        match.setPair(true);
        matchRepository.save(match);
    }

    @Override
    public void deleteMatch(Long matchId) {
        matchRepository.deleteById(matchId);
    }

}
