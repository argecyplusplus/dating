package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myprofile")
public class MyProfileController {

    private final ProfileService profileService;

    @GetMapping
    ResponseEntity<Profile> getMyProfile (){

        Optional<Profile> profileOptional = profileService.getMyProfile();
        if (profileOptional.isPresent()){
            return ResponseEntity.ok(profileOptional.get());
        }
        else{
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping
    ResponseEntity<Profile>  createMyProfile(@RequestBody Profile newProfile){
        return ResponseEntity.ok(profileService.editOrCreateMyProfile(newProfile));
    }

    @PutMapping
    ResponseEntity<Profile>  editMyProfile(@RequestBody Profile newProfile){
        return ResponseEntity.ok(profileService.editOrCreateMyProfile(newProfile));
    }

    @DeleteMapping
    ResponseEntity<Profile>  deleteMyProfile(){
        profileService.deleteMyProfile();
        return ResponseEntity.ok().build();
    }



}
