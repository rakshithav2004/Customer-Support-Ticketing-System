package com.csts.service;

import com.csts.dto.LoginRequest;
import com.csts.dto.RegisterRequest;
import com.csts.model.User;
import com.csts.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthService();
        authService.userRepository = userRepository;
        authService.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password", "CUSTOMER");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId("id123");
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(User.Role.CUSTOMER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(User.Role.CUSTOMER, result.getRole());
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password", "CUSTOMER");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("User already exists", exception.getMessage());

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    public void testAuthenticate_Success() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User result = authService.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
    }

    @Test
       void testAuthenticate_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        User result = authService.authenticate(loginRequest);

        assertNull(result);

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
        void testAuthenticate_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        User result = authService.authenticate(loginRequest);

        assertNull(result);

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
    }
}
