package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {
    private final ProfileService profileService;

    //ВСЕ АНКЕТЫ
    @GetMapping
    public ResponseEntity<Page<Profile>> getAllProfiles(@RequestParam("page") int page){
        return ResponseEntity.ok(profileService.getAllProfiles(page));
    }

    //КОНКРЕТНАЯ АНКЕТА
    @GetMapping("/{id}")
    ResponseEntity<Profile> getProfile (@PathVariable("id") Long id){

        Optional<Profile> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()){
            return ResponseEntity.ok(profileOptional.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Profile> editProfile (@PathVariable("id") Long id, @RequestBody Profile editedProfile){

        Optional<Profile> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()) {
            try{
                Profile newProfile = profileService.editProfileById(id, editedProfile);
                return ResponseEntity.ok(newProfile);
            }
            catch (AccessDeniedException e){
                return (ResponseEntity<Profile>) ResponseEntity.status(403);
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Profile> deleteProfile (@PathVariable("id") Long id){

        Optional<Profile> profileOptional = profileService.getProfileById(id);
        if (profileOptional.isPresent()) {
            try{
                profileService.deleteProfileById(id);
                return ResponseEntity.ok().build();
            }
            catch (AccessDeniedException e){
                return (ResponseEntity<Profile>) ResponseEntity.status(403);
            }
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }




}
