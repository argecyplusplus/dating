package ru.chernyukai.projects.dating.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import ru.chernyukai.projects.dating.model.ProfileInfo;
import ru.chernyukai.projects.dating.service.MatchService;
import ru.chernyukai.projects.dating.service.ProfileService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllProfiles() {
        int page = 0;
        int minAge = 18;
        int maxAge = 100;
        Page<ProfileInfo> profileInfoPage = mock(Page.class);
        when(profileService.getAllProfiles(page, minAge, maxAge)).thenReturn(profileInfoPage);

        ResponseEntity<Page<ProfileInfo>> responseEntity = profileController.getAllProfiles(page, minAge, maxAge);

        assertEquals(profileInfoPage, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getAllProfiles(page, minAge, maxAge);
    }


    @Test
    void testGetProfile() {
        Long id = 1L;
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setId(id);
        when(profileService.getProfileById(id)).thenReturn(Optional.of(profileInfo));

        ResponseEntity<ProfileInfo> responseEntity = profileController.getProfile(id);

        assertEquals(id, responseEntity.getBody().getId());
        verify(profileService, times(1)).getProfileById(id);
    }

    @Test
    void testEditProfile() {
        Long id = 1L;
        ProfileInfo editedProfile = new ProfileInfo();
        editedProfile.setId(id);
        when(profileService.getProfileById(id)).thenReturn(Optional.of(editedProfile));
        when(profileService.editProfileById(id, editedProfile)).thenReturn(editedProfile);

        ResponseEntity<ProfileInfo> responseEntity = profileController.editProfile(id, editedProfile);

        assertEquals(id, responseEntity.getBody().getId());
        verify(profileService, times(1)).getProfileById(id);
        verify(profileService, times(1)).editProfileById(id, editedProfile);
    }

    @Test
    void testDeleteProfile() {
        Long id = 1L;
        ProfileInfo profileInfo = new ProfileInfo();
        when(profileService.getProfileById(id)).thenReturn(Optional.of(profileInfo));

        ResponseEntity<ProfileInfo> responseEntity = profileController.deleteProfile(id);

        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).deleteProfileById(id);
    }

    @Test
    void testSendLike() {
        Long id = 1L;
        String action = "like";

        ResponseEntity<ProfileInfo> responseEntity = profileController.sendLike(id, action);

        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(matchService, times(1)).sendLike(id);
    }

    @Test
    void testSendLikeInvalidAction() {
        Long id = 1L;
        String action = "invalid";

        ResponseEntity<ProfileInfo> responseEntity = profileController.sendLike(id, action);

        assertEquals(400, responseEntity.getStatusCodeValue());
        verify(matchService, never()).sendLike(id);
    }

    @Test
    void testGetProfileNotFound() {
        Long id = 1L;
        when(profileService.getProfileById(id)).thenReturn(Optional.empty());

        ResponseEntity<ProfileInfo> responseEntity = profileController.getProfile(id);

        assertEquals(404, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getProfileById(id);
    }

    @Test
    void testEditProfileNotFound() {
        Long id = 1L;
        ProfileInfo editedProfile = new ProfileInfo();
        editedProfile.setId(id);
        when(profileService.getProfileById(id)).thenReturn(Optional.empty());

        ResponseEntity<ProfileInfo> responseEntity = profileController.editProfile(id, editedProfile);

        assertEquals(404, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getProfileById(id);
        verify(profileService, never()).editProfileById(id, editedProfile);
    }

    @Test
    void testEditProfileAccessDenied() {
        Long id = 1L;
        ProfileInfo editedProfile = new ProfileInfo();
        editedProfile.setId(id);
        when(profileService.getProfileById(id)).thenReturn(Optional.of(editedProfile));
        doThrow(new AccessDeniedException("Access denied")).when(profileService).editProfileById(id, editedProfile);

        ResponseEntity<ProfileInfo> responseEntity = profileController.editProfile(id, editedProfile);

        assertEquals(403, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getProfileById(id);
        verify(profileService, times(1)).editProfileById(id, editedProfile);
    }

    @Test
    void testDeleteProfileNotFound() {
        Long id = 1L;
        when(profileService.getProfileById(id)).thenReturn(Optional.empty());

        ResponseEntity<ProfileInfo> responseEntity = profileController.deleteProfile(id);

        assertEquals(404, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getProfileById(id);
        verify(profileService, never()).deleteProfileById(id);
    }

    @Test
    void testDeleteProfileAccessDenied() {
        Long id = 1L;
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setId(id);
        when(profileService.getProfileById(id)).thenReturn(Optional.of(profileInfo));
        doThrow(new AccessDeniedException("Access denied")).when(profileService).deleteProfileById(id);

        ResponseEntity<ProfileInfo> responseEntity = profileController.deleteProfile(id);

        assertEquals(403, responseEntity.getStatusCodeValue());
        verify(profileService, times(1)).getProfileById(id);
        verify(profileService, times(1)).deleteProfileById(id);
    }
}