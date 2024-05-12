package ru.chernyukai.projects.dating.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MyProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private MyProfileController myProfileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMyProfile() {
        ProfileInfo profileInfo = new ProfileInfo();
        when(profileService.getMyProfile()).thenReturn(Optional.of(profileInfo));

        ResponseEntity<ProfileInfo> responseEntity = myProfileController.getMyProfile();

        assertEquals(profileInfo, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getMyProfile();
    }

    @Test
    void testCreateMyProfile() {
        ProfileInfo profileInfo = new ProfileInfo();
        when(profileService.editOrCreateMyProfile(profileInfo)).thenReturn(profileInfo);

        ResponseEntity<ProfileInfo> responseEntity = myProfileController.createMyProfile(profileInfo);

        assertEquals(profileInfo, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).editOrCreateMyProfile(profileInfo);
    }

    @Test
    void testEditMyProfile() {
        ProfileInfo profileInfo = new ProfileInfo();
        when(profileService.editOrCreateMyProfile(profileInfo)).thenReturn(profileInfo);

        ResponseEntity<ProfileInfo> responseEntity = myProfileController.editMyProfile(profileInfo);

        assertEquals(profileInfo, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).editOrCreateMyProfile(profileInfo);
    }

    @Test
    void testDeleteMyProfile() {
        ResponseEntity<ProfileInfo> responseEntity = myProfileController.deleteMyProfile();

        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).deleteMyProfile();
    }
}
