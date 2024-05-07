package ru.chernyukai.projects.dating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.MatchService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<ProfileInfo>> getAllMatches(){
        return ResponseEntity.ok(matchService.getMyMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileInfo> getProfileFromMatch(@PathVariable ("id") Long id){
        Optional<ProfileInfo> profileInfoOptional = matchService.getProfileFromMatch(id);
        return profileInfoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<ProfileInfo> answerForMatch(@PathVariable ("id") Long id, @RequestParam("action") String action){
        try
        {
            if (action.equals("like")) {
                matchService.createPair(id);
                return ResponseEntity.ok().build();
            } else if (action.equals("dislike")) {
                matchService.deleteMatch(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
        catch (AccessDeniedException e){
            return ResponseEntity.status(403).build();
        }
    }


}
