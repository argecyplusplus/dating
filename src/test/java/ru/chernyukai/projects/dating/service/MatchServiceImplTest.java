package ru.chernyukai.projects.dating.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.chernyukai.projects.dating.model.*;
import ru.chernyukai.projects.dating.repository.MatchRepository;
import ru.chernyukai.projects.dating.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

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
    void sendLike_AccessDeniedException() {
        Profile sender = new Profile();
        sender.setId(1L);

        Profile receiver = new Profile();
        receiver.setId(2L);

        User user = new User();
        user.setId(1L);

        when(profileRepository.getProfileByUser(any())).thenReturn(Optional.of(sender));
        when(profileRepository.getProfileById(anyLong())).thenReturn(Optional.of(receiver));
        when(matchRepository.getMatchByProfile1AndProfile2(sender, receiver)).thenReturn(Optional.of(new Match()));

        assertThrows(AccessDeniedException.class, () -> matchService.sendLike(receiver.getId()));
    }
    @Test
    void getMyMatches_ReturnsCorrectMatches() {
        // Arrange
        Profile profile1 = new Profile();
        profile1.setId(1L);

        Profile profile2 = new Profile();
        profile2.setId(2L);

        Profile profile3 = new Profile();
        profile3.setId(3L);

        Match match1 = new Match();
        match1.setId(1L);
        match1.setProfile1(profile2);
        match1.setProfile2(profile1);
        match1.setPair(false);

        Match match2 = new Match();
        match2.setId(2L);
        match2.setProfile1(profile3);
        match2.setProfile2(profile1);
        match2.setPair(false);

        List<Match> matches = new ArrayList<>();
        matches.add(match1);
        matches.add(match2);

        when(profileRepository.getProfileByUser(any(User.class))).thenReturn(Optional.of(profile1));
        when(matchRepository.getAllMatchesByProfileId(profile1.getId())).thenReturn(matches);

        List<ProfileInfo> result = matchService.getMyMatches();

        assertEquals(2, result.size());
    }

    @Test
    void getMyPairs_ReturnsCorrectPairs() {
        Profile profile1 = new Profile();
        profile1.setId(1L);

        Profile profile2 = new Profile();
        profile2.setId(2L);

        Profile profile3 = new Profile();
        profile3.setId(3L);

        Match match1 = new Match();
        match1.setId(1L);
        match1.setProfile1(profile1);
        match1.setProfile2(profile2);

        Match match2 = new Match();
        match2.setId(2L);
        match2.setProfile1(profile3);
        match2.setProfile2(profile1);

        List<Match> pairs = new ArrayList<>();
        pairs.add(match1);
        pairs.add(match2);

        when(profileRepository.getProfileByUser(any(User.class))).thenReturn(Optional.of(profile1));
        when(matchRepository.getAllPairsByProfileId(profile1.getId())).thenReturn(pairs);

        List<ProfileInfo> result = matchService.getMyPairs();

        assertEquals(2, result.size());
    }

    @Test
    void getProfileFromMatch_ReturnsEmptyOptionalIfMatchNotExists() {
        Long matchId = 1L;

        when(matchRepository.getMatchById(matchId)).thenReturn(Optional.empty());

        Optional<ProfileInfo> result = matchService.getProfileFromMatch(matchId);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void getProfileFromPair_ReturnsEmptyOptionalIfPairNotExistsOrUserHasNoAccess() {

        Long matchId = 1L;
        Profile myProfile = new Profile();
        myProfile.setId(1L);
        Match match = new Match();
        match.setId(matchId);
        match.setProfile1(new Profile());
        match.setProfile2(new Profile());

        when(matchRepository.getMatchById(matchId)).thenReturn(Optional.of(match));
        when(profileRepository.getProfileByUser(mock(User.class))).thenReturn(Optional.of(myProfile));


        Optional<ProfileInfo> result = matchService.getProfileFromPair(matchId);


        assertEquals(Optional.empty(), result);
    }


    @Test
    void createPair_CreatesPairIfMatchExistsAndUserHasAccess() {

        Profile profile = new Profile();
        profile.setId(1L);

        Long matchId = 1L;
        Match match = new Match();
        match.setId(matchId);
        match.setProfile2(profile);
        match.setPair(false);

        when(matchRepository.getMatchById(matchId)).thenReturn(Optional.of(match));
        when(profileRepository.getProfileByUser(any(User.class))).thenReturn(Optional.of(profile));

        assertDoesNotThrow(() -> matchService.createPair(matchId));

        assertTrue(match.isPair());
        verify(matchRepository, times(1)).save(match);
    }

}