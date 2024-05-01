package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chernyukai.projects.dating.model.ProfileInfo;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public ResponseEntity<List<ProfileInfo>> homePage(){
        return ResponseEntity.ok().build();
    }
}
