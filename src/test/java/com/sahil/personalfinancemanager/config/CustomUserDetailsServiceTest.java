package com.sahil.personalfinancemanager.config;

import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_UserExists() {
        User user = new User(
                "test@test.com",
                "encodedPassword",
                "Test User",
                "9876543210"
        );

        when(userRepository.findByUsername("test@test.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().isEmpty());

        verify(userRepository).findByUsername("test@test.com");
    }

    @Test
    void loadUserByUsername_UserDoesNotExist() {
        when(userRepository.findByUsername("missing@test.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> service.loadUserByUsername("missing@test.com")
                );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("missing@test.com");
    }
}