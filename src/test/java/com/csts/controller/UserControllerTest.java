package com.csts.controller;

import com.csts.model.User;
import com.csts.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_success() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        List<?> bodyList = (List<?>) response.getBody();
        assertEquals(2, bodyList.size());
    }

    @Test
    void getAllUsers_error() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("DB error"));
    }

    @Test
    void getMyProfile_success() {
        User user = new User();
        user.setEmail("user@example.com");

        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getMyProfile("user@example.com")).thenReturn(user);

        ResponseEntity<?> response = userController.getMyProfile(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);
        assertEquals("user@example.com", ((User) response.getBody()).getEmail());
    }

    @Test
    void getMyProfile_error() {
        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getMyProfile("user@example.com")).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<?> response = userController.getMyProfile(authentication);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Not found"));
    }

    @Test
    void getUserById_success() {
        User user = new User();
        user.setId("123");

        when(userService.getUserById("123")).thenReturn(user);

        ResponseEntity<?> response = userController.getUserById("123");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof User);
        assertEquals("123", ((User) response.getBody()).getId());
    }

    @Test
    void getUserById_error() {
        when(userService.getUserById("999")).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<?> response = userController.getUserById("999");

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("User not found"));
    }
}