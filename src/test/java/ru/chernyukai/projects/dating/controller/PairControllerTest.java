package ru.chernyukai.projects.dating.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.MatchService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PairControllerTest {

    @Mock
    private MatchService matchService;

    @InjectMocks
    private PairController pairController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPairs() {
        List<ProfileInfo> expectedProfiles = Collections.singletonList(new ProfileInfo());
        when(matchService.getMyPairs()).thenReturn(expectedProfiles);

        ResponseEntity<List<ProfileInfo>> response = pairController.getAllPairs();

        assertEquals(expectedProfiles, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProfileFromPair_Success() {
        Long pairId = 1L;
        ProfileInfo expectedProfileInfo = new ProfileInfo();
        when(matchService.getProfileFromPair(pairId)).thenReturn(Optional.of(expectedProfileInfo));

        ResponseEntity<ProfileInfo> response = pairController.getProfileFromPair(pairId);

        assertEquals(expectedProfileInfo, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProfileFromPair_NotFound() {
        Long pairId = 1L;
        when(matchService.getProfileFromPair(pairId)).thenReturn(Optional.empty());

        ResponseEntity<ProfileInfo> response = pairController.getProfileFromPair(pairId);

        assertEquals(404, response.getStatusCodeValue());
    }
}
