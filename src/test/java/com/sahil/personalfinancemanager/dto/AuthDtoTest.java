package com.sahil.personalfinancemanager.dto.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDtoTest {

    @Test
    void authResponse_AllAccessors() {
        AuthResponse response = new AuthResponse(
                1L,
                "test@test.com",
                "Test User",
                "Login successful"
        );

        assertEquals(1L, response.userId());
        assertEquals("test@test.com", response.username());
        assertEquals("Test User", response.fullName());
        assertEquals("Login successful", response.message());
    }

    @Test
    void loginRequest_AllAccessors() {
        LoginRequest request = new LoginRequest(
                "test@test.com",
                "Password123!"
        );

        assertEquals("test@test.com", request.username());
        assertEquals("Password123!", request.password());
    }

    @Test
    void registerRequest_AllAccessors() {
        RegisterRequest request = new RegisterRequest(
                "test@test.com",
                "Password123!",
                "Test User",
                "9876543210"
        );

        assertEquals("test@test.com", request.username());
        assertEquals("Password123!", request.password());
        assertEquals("Test User", request.fullName());
        assertEquals("9876543210", request.phoneNumber());
    }
}