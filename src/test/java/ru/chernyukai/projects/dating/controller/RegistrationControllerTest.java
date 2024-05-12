package ru.chernyukai.projects.dating.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.chernyukai.projects.dating.exceptions.UsernameAlreadyExistsException;
import ru.chernyukai.projects.dating.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void registration_NewUser_Success() {
        String username = "testuser";
        String password = "password";

        ResponseEntity<Void> response = registrationController.registration(username, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).registration(username, password);
    }

    @Test
    void registration_ExistingUser_ReturnsConflict() {
        String username = "existingUser";
        String password = "password";
        doThrow(UsernameAlreadyExistsException.class).when(userService).registration(username, password);

        ResponseEntity<Void> response = registrationController.registration(username, password);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userService, times(1)).registration(username, password);
    }
    @Test
    void registration_InvalidParameters_ReturnsBadRequest() {
        String username = "test";
        String password = null;

        ResponseEntity<Void> response = registrationController.registration(username, password);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).registration(any(), any());
    }

}