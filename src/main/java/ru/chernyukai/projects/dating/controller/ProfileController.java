package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.chernyukai.projects.dating.model.Match;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.service.MatchService;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final MatchService matchService;

    //ВСЕ АНКЕТЫ
    @GetMapping
    public ResponseEntity<Page<ProfileInfo>> getAllProfiles(@RequestParam("page") int page, @RequestParam("min_age") int minAge, @RequestParam("max_age") int maxAge){
        return ResponseEntity.ok(profileService.getAllProfiles(page, minAge, maxAge));
    }

    //КОНКРЕТНАЯ АНКЕТА
    @GetMapping("/{id}")
    ResponseEntity<ProfileInfo> getProfile (@PathVariable("id") Long id){

        Optional<ProfileInfo> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()){
            return ResponseEntity.ok(profileOptional.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<ProfileInfo> editProfile (@PathVariable("id") Long id, @RequestBody ProfileInfo editedProfile){

        Optional<ProfileInfo> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()) {
            try{
                ProfileInfo newProfile = profileService.editProfileById(id, editedProfile);
                return ResponseEntity.ok(newProfile);
            }
            catch (AccessDeniedException e){
                return (ResponseEntity<ProfileInfo>) ResponseEntity.status(403);
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ProfileInfo> deleteProfile (@PathVariable("id") Long id){

        Optional<ProfileInfo> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()) {
            try{
                profileService.deleteProfileById(id);
                return ResponseEntity.ok().build();
            }
            catch (AccessDeniedException e){
                return (ResponseEntity<ProfileInfo>) ResponseEntity.status(403);
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    //Отправить лайк
    @PostMapping("/{id}")
    ResponseEntity<ProfileInfo> deleteProfile (@PathVariable("id") Long id, @RequestParam("action") String action){
        matchService.sendLike(id);
        return ResponseEntity.ok().build();
    }




}
