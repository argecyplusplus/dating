package ru.chernyukai.projects.dating.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.chernyukai.projects.dating.components.ProfilePhotoConverter;
import ru.chernyukai.projects.dating.model.*;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService{
    @Autowired
    MatchRepository matchRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ProfileService profileService;

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean userIsAdmin (){
        User user = getCurrentUser();
        return user.getAuthorities().stream()
                .filter(authority -> authority instanceof UserAuthority)
                .map(authority -> (UserAuthority) authority)
                .anyMatch(UserAuthority.ADMIN::equals);
    }

    public boolean checkAccessToProfile(Profile profile) {
        User user = getCurrentUser();

        //Если зашел админ
        if (userIsAdmin()){
            return true;
        }

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        //Если не заполнен свой профиль
        if (myProfileOptional.isEmpty()){
            return false;
        }
        Profile myProfile = myProfileOptional.get();
        //Если видишь свой профиль
        if(Objects.equals(profile.getId(), myProfile.getId())){
            return false;
        }

        //Твой профиль отключен
        if(!myProfile.isVisible()){
            return false;
        }

        //Проверка на пол
        if (profile.getGender().equals(myProfile.getGender())){
            return false;
        }

        //Проверка на город
        if (!profile.getCity().equals(myProfile.getCity())){
            return false;
        }

        return true;
    }

    ProfilePhotoConverter converter = new ProfilePhotoConverter();

    public boolean checkAccessToMatch(Long matchId){
        User user = getCurrentUser();
        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        if (myProfileOptional.isEmpty()){
            return false;
        }

        Profile myProfile = myProfileOptional.get();

        Match match = matchRepository.getMatchById(matchId).get();

        Profile profile2 = match.getProfile2();

        //Проверка что юзер получатель
        return myProfile.getId().equals(profile2.getId());

    }



    public boolean checkAccessToPair (Long matchId){
        User user = getCurrentUser();

        Optional<Profile> myProfileOptional = profileRepository.getProfileByUser(user);

        if (myProfileOptional.isEmpty()){
            return false;
        }

        Profile myProfile = myProfileOptional.get();

        Match match = matchRepository.getMatchById(matchId).get();

        Profile profile1 = match.getProfile1();
        Profile profile2 = match.getProfile2();

        //Проверка что юзер есть в мэтче
        return myProfile.getId().equals(profile1.getId())
                || myProfile.getId().equals(profile2.getId());

    }


    @Override
    public void sendLike (Long id) throws AccessDeniedException{

        Profile sender = profileRepository.getProfileByUser(getCurrentUser()).get();
        Profile receiver = profileRepository.getProfileById(id).get();

        if (!checkAccessToProfile(receiver)){
            throw new AccessDeniedException ("Нет доступа к этому мэтчу");
        }


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
                        profile.getPhotos() != null ?
                                profile.getPhotos().stream()
                                        .flatMap(photo -> converter.convertProfilePhotosToMultipartFiles(profile.getPhotos()).stream())
                                        .collect(Collectors.toList()) :
                                Collections.emptyList(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getInterests() != null ?
                                profile.getInterests().stream()
                                        .map(interest -> {
                                            InterestValue value = interest.getValue();
                                            return value != null ? value.getTitle() : null;
                                        })
                                        .collect(Collectors.toList()) :
                                Collections.emptyList(),
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
                        profile.getPhotos() != null ?
                                profile.getPhotos().stream()
                                        .flatMap(photo -> converter.convertProfilePhotosToMultipartFiles(profile.getPhotos()).stream())
                                        .collect(Collectors.toList()) :
                                Collections.emptyList(),
                        profile.getCity(),
                        profile.getGender(),
                        profile.getInterests() != null ?
                                profile.getInterests().stream()
                                        .map(interest -> {
                                            InterestValue value = interest.getValue();
                                            return value != null ? value.getTitle() : null;
                                        })
                                        .collect(Collectors.toList()) :
                                Collections.emptyList(),

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
                            profile.getPhotos() != null ?
                                    profile.getPhotos().stream()
                                            .flatMap(photo -> converter.convertProfilePhotosToMultipartFiles(profile.getPhotos()).stream())
                                            .collect(Collectors.toList()) :
                                    Collections.emptyList(),
                            profile.getCity(),
                            profile.getGender(),
                            profile.getInterests().stream()
                                    .map(interest -> {
                                        InterestValue value = interest.getValue();
                                        return value != null ? value.getTitle() : null;
                                    })
                                    .collect(Collectors.toList()),

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
        if (matchOptional.isPresent() && checkAccessToPair(matchId)){
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
                            profile.getPhotos() != null ?
                                    profile.getPhotos().stream()
                                            .flatMap(photo -> converter.convertProfilePhotosToMultipartFiles(profile.getPhotos()).stream())
                                            .collect(Collectors.toList()) :
                                    Collections.emptyList(),
                            profile.getCity(),
                            profile.getGender(),
                            profile.getInterests().stream()
                                    .map(interest -> {
                                        InterestValue value = interest.getValue();
                                        return value != null ? value.getTitle() : null;
                                    })
                                    .collect(Collectors.toList()),

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
    public void createPair(Long matchId) throws AccessDeniedException {

        Match match = matchRepository.getMatchById(matchId).get();
        if (checkAccessToMatch(matchId)){
            match.setPair(true);
            matchRepository.save(match);
        }
        else{
            throw new AccessDeniedException("Нет доступа к этому мэтчу");
        }

    }

    @Override
    public void deleteMatch(Long matchId) throws AccessDeniedException {
        if (checkAccessToMatch(matchId)) {
            matchRepository.deleteById(matchId);
        }
        else {
            throw new AccessDeniedException("Нет доступа к этому мэтчу");
        }
    }

}
