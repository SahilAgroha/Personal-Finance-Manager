package com.sahil.personalfinancemanager.dto.auth;

public record AuthResponse(
        Long userId,
        String username,
        String fullName,
        String message
) {}