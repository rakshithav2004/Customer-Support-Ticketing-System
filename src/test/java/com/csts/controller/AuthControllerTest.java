package com.csts.controller;

import com.csts.dto.LoginRequest;
import com.csts.dto.LoginResponse;
import com.csts.dto.RegisterRequest;
import com.csts.model.User;
import com.csts.security.OpaqueTokenService;
import com.csts.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.csts.model.User.Role.CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private OpaqueTokenService tokenService;
    private AuthController authController;

    @BeforeEach
        void setUp() {
        authService = Mockito.mock(AuthService.class);
        tokenService = Mockito.mock(OpaqueTokenService.class);
        authController = new AuthController();
        try {
            java.lang.reflect.Field authServiceField = AuthController.class.getDeclaredField("authService");
            authServiceField.setAccessible(true);
            authServiceField.set(authController, authService);

            java.lang.reflect.Field tokenServiceField = AuthController.class.getDeclaredField("tokenService");
            tokenServiceField.setAccessible(true);
            tokenServiceField.set(authController, tokenService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
        void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password","CUSTOMER");
        User user = new User( "John Doe", "john@example.com", "hashedPassword", User.Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(user);

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof User);
        assertEquals("John Doe", ((User)response.getBody()).getName());
    }

    @Test
        void testRegister_Failure() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password", "CUSTOMER");

        when(authService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Registration failed"));

        ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed", response.getBody());
    }

    @Test
        void testLogin_Success() {
        LoginRequest request = new LoginRequest("john@example.com", "password");
        User user = new User("John Doe", "john@example.com", "hashedPassword", CUSTOMER);

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("mock-token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof LoginResponse);
        assertEquals("mock-token", ((LoginResponse)response.getBody()).getToken());
    }

    @Test
        void testLogin_InvalidCredentials() {
        LoginRequest request = new LoginRequest("john@example.com", "wrongpassword");

        when(authService.authenticate(any(LoginRequest.class))).thenReturn(null);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
        void testLogin_Exception() {
        LoginRequest request = new LoginRequest("john@example.com", "password");

        when(authService.authenticate(any(LoginRequest.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Login failed: Database error", response.getBody());
    }
}
