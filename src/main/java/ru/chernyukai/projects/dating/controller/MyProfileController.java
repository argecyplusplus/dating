package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chernyukai.projects.dating.exceptions.InvalidProfileException;
import ru.chernyukai.projects.dating.model.Profile;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myprofile")
public class MyProfileController {

    private final ProfileService profileService;

    @GetMapping
    ResponseEntity<ProfileInfo> getMyProfile (){

        Optional<ProfileInfo> profileOptional = profileService.getMyProfile();
        if (profileOptional.isPresent()){
            return ResponseEntity.ok(profileOptional.get());
        }
        else{
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping
    ResponseEntity<ProfileInfo>  createMyProfile(@RequestBody ProfileInfo newProfile){
        try{
            return ResponseEntity.ok(profileService.editOrCreateMyProfile(newProfile));
        }
        catch (InvalidProfileException e){
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping
    ResponseEntity<ProfileInfo>  editMyProfile(@RequestBody ProfileInfo newProfile){
        try{
            return ResponseEntity.ok(profileService.editOrCreateMyProfile(newProfile));
        }
        catch (InvalidProfileException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    ResponseEntity<ProfileInfo>  deleteMyProfile(){
        profileService.deleteMyProfile();
        return ResponseEntity.ok().build();
    }



}
