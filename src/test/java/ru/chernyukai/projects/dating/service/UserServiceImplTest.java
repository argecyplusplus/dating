package ru.chernyukai.projects.dating.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.chernyukai.projects.dating.exceptions.UsernameAlreadyExistsException;
import ru.chernyukai.projects.dating.model.User;
import ru.chernyukai.projects.dating.repository.UserRepository;
import ru.chernyukai.projects.dating.repository.UserRolesRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registration_NewUser_Success() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedpassword");

        // Act
        assertDoesNotThrow(() -> userService.registration(username, password));

        // Assert
        verify(userRepository, times(1)).save(any());
        verify(userRolesRepository, times(1)).save(any());
    }

    @Test
    void registration_ExistingUser_ThrowsException() {
        // Arrange
        String username = "existinguser";
        String password = "testpassword";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mock(User.class)));

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registration(username, password));
        verify(userRepository, never()).save(any());
        verify(userRolesRepository, never()).save(any());
    }

    @Test
    void registration_NullUsername_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registration(null, "testpassword"));
        verify(userRepository, never()).save(any());
        verify(userRolesRepository, never()).save(any());
    }

    @Test
    void registration_NullPassword_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registration("testuser", null));
        verify(userRepository, never()).save(any());
        verify(userRolesRepository, never()).save(any());
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        // Arrange
        String username = "testuser";
        User user = new User().setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsException() {
        // Arrange
        String username = "nonexistinguser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }
}