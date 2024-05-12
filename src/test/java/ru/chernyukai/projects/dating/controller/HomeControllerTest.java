package ru.chernyukai.projects.dating.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.chernyukai.projects.dating.model.ProfileInfo;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHomePage() {
        ResponseEntity<List<ProfileInfo>> responseEntity = homeController.homePage();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(null, responseEntity.getBody());
    }
}
