package com.sahil.personalfinancemanager.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void badRequest() {
        BadRequestException ex = new BadRequestException("Bad request error");
        Map<String, String> response = exceptionHandler.badRequest(ex);

        assertEquals("Bad Request", response.get("error"));
        assertEquals("Bad request error", response.get("message"));
    }

    @Test
    void conflict() {
        ConflictException ex = new ConflictException("Conflict error");
        Map<String, String> response = exceptionHandler.conflict(ex);

        assertEquals("Conflict", response.get("error"));
        assertEquals("Conflict error", response.get("message"));
    }

    @Test
    void notFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found error");
        Map<String, String> response = exceptionHandler.notFound(ex);

        assertEquals("Not Found", response.get("error"));
        assertEquals("Not found error", response.get("message"));
    }

    @Test
    void validationError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("objectName", "field", "defaultMessage")));

        Map<String, Object> response = exceptionHandler.validationError(ex);

        assertEquals("Bad Request", response.get("error"));
        assertEquals("field: defaultMessage", response.get("message"));
    }

    @Test
    void badCredentials() {
        org.springframework.security.authentication.BadCredentialsException ex = new org.springframework.security.authentication.BadCredentialsException("bad");
        Map<String, String> response = exceptionHandler.badCredentials(ex);

        assertEquals("Unauthorized", response.get("error"));
        assertEquals("Invalid username or password", response.get("message"));
    }
    
    @Test
    void malformedRequest() {
        // HttpMessageNotReadableException has no simple constructor; test via mocking the handler is sufficient
        // Verify the handler method is reachable via the RestControllerAdvice annotation
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        org.springframework.http.converter.HttpMessageNotReadableException ex =
                org.mockito.Mockito.mock(org.springframework.http.converter.HttpMessageNotReadableException.class);
        Map<String, String> response = handler.malformedRequest(ex);

        assertEquals("Bad Request", response.get("error"));
        assertEquals("Invalid request format", response.get("message"));
    }
}
