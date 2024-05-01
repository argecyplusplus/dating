package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.MatchService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pairs")
public class PairController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<ProfileInfo>> getAllPairs(){
        return ResponseEntity.ok(matchService.getMyPairs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileInfo> getProfileFromPair(@PathVariable("id") Long id){
        Optional<ProfileInfo> profileInfoOptional = matchService.getProfileFromPair(id);
        return profileInfoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
