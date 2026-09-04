package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.auth.LoginRequest;
import com.sahil.personalfinancemanager.dto.auth.RegisterRequest;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.ConflictException;
import com.sahil.personalfinancemanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private HttpSession session;

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("test@test.com", "password", "Test User", "1234567890");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        
        User savedUser = new User("test@test.com", "encodedPassword", "Test User", "1234567890");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Map<String, Object> response = authService.register(request);

        assertEquals("User registered successfully", response.get("message"));
        assertEquals(1L, response.get("userId"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists_ThrowsConflictException() {
        RegisterRequest request = new RegisterRequest("test@test.com", "password", "Test User", "1234567890");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        Authentication authentication = mock(Authentication.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Map<String, String> response = authService.login(request, httpRequest, httpResponse);

        assertEquals("Login successful", response.get("message"));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void logout_WithSession() {
        when(httpRequest.getSession(false)).thenReturn(session);

        Map<String, String> response = authService.logout(httpRequest);

        assertEquals("Logout successful", response.get("message"));
        verify(session).invalidate();
    }

    @Test
    void logout_WithoutSession() {
        when(httpRequest.getSession(false)).thenReturn(null);

        Map<String, String> response = authService.logout(httpRequest);

        assertEquals("Logout successful", response.get("message"));
    }
}
