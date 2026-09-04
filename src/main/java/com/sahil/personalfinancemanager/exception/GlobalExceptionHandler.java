package com.sahil.personalfinancemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validationError(
            MethodArgumentNotValidException exception
    ) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .orElse("Invalid request");

        return Map.of(
                "error", "Bad Request",
                "message", message
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(
            BadRequestException exception
    ) {
        return Map.of(
                "error", "Bad Request",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(
            ConflictException exception
    ) {
        return Map.of(
                "error", "Conflict",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(
            ResourceNotFoundException exception
    ) {
        return Map.of(
                "error", "Not Found",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(
            org.springframework.security.authentication.BadCredentialsException.class
    )
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> badCredentials(
            org.springframework.security.authentication.BadCredentialsException exception
    ) {
        return Map.of(
                "error", "Unauthorized",
                "message", "Invalid username or password"
        );
    }

    @ExceptionHandler(
            org.springframework.http.converter.HttpMessageNotReadableException.class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> malformedRequest(
            org.springframework.http.converter.HttpMessageNotReadableException exception
    ) {
        return Map.of(
                "error", "Bad Request",
                "message", "Invalid request format"
        );
    }
}