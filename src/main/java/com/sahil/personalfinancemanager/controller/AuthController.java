package com.sahil.personalfinancemanager.controller;

import com.sahil.personalfinancemanager.dto.auth.LoginRequest;
import com.sahil.personalfinancemanager.dto.auth.RegisterRequest;
import com.sahil.personalfinancemanager.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public Map<String, String> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        return authService.login(
                request,
                httpRequest,
                httpResponse
        );
    }

    // =========================
    // LOGOUT
    // =========================

    @PostMapping("/logout")
    public Map<String, String> logout(
            HttpServletRequest request
    ) {
        return authService.logout(request);
    }
}