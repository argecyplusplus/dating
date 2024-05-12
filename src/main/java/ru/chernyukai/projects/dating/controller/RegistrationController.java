package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.chernyukai.projects.dating.exceptions.UsernameAlreadyExistsException;
import ru.chernyukai.projects.dating.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reg")
public class RegistrationController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> registration(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {

        if (username == null || password == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try{
            userService.registration(username, password);
            return ResponseEntity.ok().build();
        }
        catch (UsernameAlreadyExistsException e){
            return ResponseEntity.status(409).build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).build();
        }


    }
}
