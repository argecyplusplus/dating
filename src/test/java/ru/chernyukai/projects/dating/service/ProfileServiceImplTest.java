package ru.chernyukai.projects.dating.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import ru.chernyukai.projects.dating.components.CustomMultipartFile;
import ru.chernyukai.projects.dating.model.*;
import ru.chernyukai.projects.dating.repository.InterestRepository;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.repository.PhotoRepository;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private PhotoRepository photoRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        User user = new User();

        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setExpired(false);
        user.setLocked(false);
        user.setEnabled(true);

        List<UserRole> roles = new ArrayList<>();
        UserRole role = new UserRole();
        role.setId(1L);
        role.setUserAuthority(UserAuthority.DEFAULT_USER);
        role.setUser(user);
        roles.add(role);
        user.setUserRoles(roles);

        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUser_ReturnsCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        User user = new User();
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User currentUser = profileService.getCurrentUser();

        assertEquals(user, currentUser);
    }


    @Test
    void getAllProfiles_ReturnsFilteredProfiles() {

        Profile myProfile = new Profile();
        when(profileRepository.getProfileByUser(any())).thenReturn(Optional.of(myProfile));

        when(profileRepository.findProfilesBy()).thenReturn(Collections.emptyList());
        when(matchRepository.getPairByProfile1AndProfile2(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            Page<ProfileInfo> result = profileService.getAllProfiles(0, 18, 100);
            assertNotNull(result);
        });
    }

    @Test
    void getProfileById_ReturnsProfileInfoIfAccessible() {
        // Setup security context
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Create profile
        Profile myProfile = new Profile();
        myProfile.setId(2L);
        myProfile.setVisible(true);
        myProfile.setGender("Мужской");
        myProfile.setCity("Москва");
        List<ProfilePhoto> myPhotos = new ArrayList<>();
        myPhotos.add(new ProfilePhoto(null,"/fff/", "image/jpeg", new byte[]{}, myProfile));
        myProfile.setPhotos(myPhotos);
        List<Interest> myInterests = new ArrayList<>();
        myInterests.add(new Interest(null,InterestValue.ART, myProfile));
        myProfile.setInterests(myInterests);
        when(profileRepository.getProfileByUser(any(User.class))).thenReturn(Optional.of(myProfile));

        // Create expected profile
        long profileId = 1L;
        Profile expectedProfile = new Profile();
        expectedProfile.setId(profileId);
        expectedProfile.setGender("Женский");
        expectedProfile.setCity("Москва");
        expectedProfile.setVisible(true);
        List<ProfilePhoto> photos = new ArrayList<>();
        photos.add(new ProfilePhoto(null,"/fff/", "image/jpeg", new byte[]{}, myProfile));
        expectedProfile.setPhotos(photos);
        List<Interest> interests = new ArrayList<>();
        interests.add(new Interest(null,InterestValue.ART, expectedProfile));
        expectedProfile.setInterests(interests);
        when(profileRepository.getProfileById(profileId)).thenReturn(Optional.of(expectedProfile));

        // Call service method
        Optional<ProfileInfo> profileInfoOptional = profileService.getProfileById(profileId);

        // Verify result
        assertTrue(profileInfoOptional.isPresent());
        assertEquals(profileId, profileInfoOptional.get().getId());
    }

    @Test
    void editProfileById_ReturnsEditedProfileInfoIfAdmin() {
        Profile profile = new Profile();
        when(profileRepository.getProfileById(anyLong())).thenReturn(Optional.empty());

        when(profileService.userIsAdmin()).thenReturn(true);

        List<MultipartFile> photos = new ArrayList<>();
        photos.add(new CustomMultipartFile("photo1", "photo1.jpg", "image/jpeg", new byte[]{}));
        List<String> interests = new ArrayList<>();
        interests.add("Аниме");

        ProfileInfo editedProfile = new ProfileInfo(
                profile.getId(),
                "Алёша",
                20,
                photos,
                "Москва",
                "Мужской",
                interests,
                "ABC",
                "t.me/asd"
        );

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            ProfileInfo result = profileService.editProfileById(profile.getId(), editedProfile);
            assertNull(result);

        });
    }


    @Test
    void deleteProfileById_RemovesProfileForNormalUser() {
        Authentication authentication = mock(Authentication.class);
        User user = new User();

        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setExpired(false);
        user.setLocked(false);
        user.setEnabled(true);

        List<UserRole> roles = new ArrayList<>();
        UserRole role = new UserRole();
        role.setId(1L);
        role.setUserAuthority(UserAuthority.ADMIN);
        role.setUser(user);
        roles.add(role);
        user.setUserRoles(roles);

        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(profileService.getCurrentUser()).thenReturn(user);

        Profile profile = new Profile();
        profile.setId(100L);

        when(profileRepository.getProfileById(profile.getId())).thenReturn(Optional.of(profile));

        assertDoesNotThrow(() -> profileService.deleteProfileById(profile.getId()));
    }


    @Test
    void getMyProfile_ReturnsEmptyOptionalIfProfileNotExists() {
        when(profileRepository.getProfileByUser(any())).thenReturn(Optional.empty());

        Optional<ProfileInfo> result = profileService.getMyProfile();

        assertTrue(result.isEmpty());
    }

    @Test
    void editOrCreateMyProfile_ReturnsEditedProfileInfo() throws IOException{

        ProfileInfo newProfile = new ProfileInfo();
        newProfile.setName("New Test Profile");
        newProfile.setAge(20);
        newProfile.setGender("Мужской");
        newProfile.setCity("Ярославль");
        newProfile.setSocialLink("t.me/123123");

        ProfileInfo result = profileService.editOrCreateMyProfile(newProfile);

        assertNotNull(result);
        assertEquals(newProfile.getName(), result.getName());
    }

    @Test
    void deleteMyProfile_DeletesProfileIfExists() {
        Profile profile = new Profile();
        profile.setId(1L);
        when(profileRepository.getProfileByUser(any())).thenReturn(Optional.of(profile));

        profileService.deleteMyProfile();

        verify(profileRepository, times(1)).delete(profile);
    }

    @Test
    void deleteMyProfile_DoesNothingIfProfileNotExists() {
        when(profileRepository.getProfileByUser(any())).thenReturn(Optional.empty());

        profileService.deleteMyProfile();

        verify(profileRepository, never()).delete(any());
    }



    @Test
    void updateInterests_ReturnsNewInterests() {
        Profile profile = new Profile();
        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(profile));
        when(interestRepository.save(any())).thenReturn(new Interest());

        List<String> newInterestsList = new ArrayList<>();
        newInterestsList.add(InterestValue.ART.getTitle());

        List<Interest> newInterests = profileService.updateInterests(profile.getId(), newInterestsList);

        assertNotNull(newInterests);
    }

    @Test
    void updatePhotos_ReturnsNewPhotos() throws IOException {
        Profile profile = new Profile();
        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(profile));
        when(photoRepository.save(any())).thenReturn(new ProfilePhoto());

        List<ProfilePhoto> newPhotos = profileService.updatePhotos(profile.getId(), new ArrayList<MultipartFile>());

        assertNotNull(newPhotos);
    }

    @Test
    void countCommonInterests_ReturnsCommonInterestsCount() {
        Profile profile1 = new Profile();
        Profile profile2 = new Profile();
        Interest interestAnime = new Interest(null, InterestValue.ANIME, profile1);

        List<Interest> interests1 = new ArrayList<>();
        interests1.add(interestAnime);

        List<Interest> interests2 = new ArrayList<>();
        interests1.add(interestAnime);

        profile1.setInterests(interests1);
        profile2.setInterests(interests2);


        int commonInterestsCount = profileService.countCommonInterests(profile1, profile2);
    }

}
