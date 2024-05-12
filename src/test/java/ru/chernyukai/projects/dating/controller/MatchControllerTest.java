package ru.chernyukai.projects.dating.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.MatchService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchControllerTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMatches() {
        List<ProfileInfo> expectedProfiles = Collections.singletonList(new ProfileInfo());
        when(matchService.getMyMatches()).thenReturn(expectedProfiles);

        ResponseEntity<List<ProfileInfo>> response = matchController.getAllMatches();

        assertEquals(expectedProfiles, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProfileFromMatch_Success() {
        Long matchId = 1L;
        ProfileInfo expectedProfileInfo = new ProfileInfo();
        when(matchService.getProfileFromMatch(matchId)).thenReturn(Optional.of(expectedProfileInfo));

        ResponseEntity<ProfileInfo> response = matchController.getProfileFromMatch(matchId);

        assertEquals(expectedProfileInfo, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProfileFromMatch_NotFound() {
        Long matchId = 1L;
        when(matchService.getProfileFromMatch(matchId)).thenReturn(Optional.empty());

        ResponseEntity<ProfileInfo> response = matchController.getProfileFromMatch(matchId);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testAnswerForMatch_Like() {
        Long matchId = 1L;
        doNothing().when(matchService).createPair(matchId);

        ResponseEntity<ProfileInfo> response = matchController.answerForMatch(matchId, "like");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testAnswerForMatch_Dislike() {
        Long matchId = 1L;
        doNothing().when(matchService).deleteMatch(matchId);
        ResponseEntity<ProfileInfo> response = matchController.answerForMatch(matchId, "dislike");

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testAnswerForMatch_BadRequest() {
        Long matchId = 1L;

        ResponseEntity<ProfileInfo> response = matchController.answerForMatch(matchId, "unknown");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testAnswerForMatch_AccessDenied() {
        Long matchId = 1L;
        doThrow(new AccessDeniedException("Access denied")).when(matchService).createPair(matchId);

        ResponseEntity<ProfileInfo> response = matchController.answerForMatch(matchId, "like");

        assertEquals(403, response.getStatusCodeValue());
    }
}