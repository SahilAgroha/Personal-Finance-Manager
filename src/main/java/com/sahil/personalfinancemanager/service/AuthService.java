package com.sahil.personalfinancemanager.service;

import com.sahil.personalfinancemanager.dto.auth.LoginRequest;
import com.sahil.personalfinancemanager.dto.auth.RegisterRequest;
import com.sahil.personalfinancemanager.entity.User;
import com.sahil.personalfinancemanager.exception.ConflictException;
import com.sahil.personalfinancemanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // =========================
    // REGISTER
    // =========================

    public Map<String, Object> register(
            RegisterRequest request
    ) {

        // Check duplicate username
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException(
                    "Username already exists"
            );
        }

        // Create user
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                request.phoneNumber()
        );

        // Save user
        User savedUser = userRepository.save(user);

        return Map.of(
                "message", "User registered successfully",
                "userId", savedUser.getId()
        );
    }

    // =========================
    // LOGIN
    // =========================

    public Map<String, String> login(
            LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        /*
         * Authenticate username + password.
         *
         * If credentials are invalid,
         * AuthenticationManager throws an authentication exception.
         */
        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                request.username(),
                                request.password()
                        )
                );

        /*
         * Create a new SecurityContext.
         */
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        /*
         * Store authenticated user in SecurityContext.
         */
        context.setAuthentication(authentication);

        /*
         * Set context for current request.
         */
        SecurityContextHolder.setContext(context);

        /*
         * Persist SecurityContext into HTTP session.
         *
         * This is important because subsequent requests
         * should remain authenticated.
         */
        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return Map.of(
                "message", "Login successful"
        );
    }

    // =========================
    // LOGOUT
    // =========================

    public Map<String, String> logout(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return Map.of(
                "message", "Logout successful"
        );
    }
}