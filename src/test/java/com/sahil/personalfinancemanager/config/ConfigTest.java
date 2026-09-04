package com.sahil.personalfinancemanager.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.mockito.Mockito.*;

public class ConfigTest {

    @Test
    void goalAmountSerializer_Null() throws Exception {
        GoalAmountSerializer serializer = new GoalAmountSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(null, gen, context);

        verify(gen).writeNull();
    }

    @Test
    void goalAmountSerializer_Zero() throws Exception {
        GoalAmountSerializer serializer = new GoalAmountSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(BigDecimal.ZERO, gen, context);

        verify(gen).writeNumber(0);
    }

    @Test
    void goalAmountSerializer_NonZero() throws Exception {
        GoalAmountSerializer serializer = new GoalAmountSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(new BigDecimal("5000.123"), gen, context);

        verify(gen).writeNumber(new BigDecimal("5000.12"));
    }

    @Test
    void goalPercentageSerializer_Null() throws Exception {
        GoalPercentageSerializer serializer = new GoalPercentageSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(null, gen, context);

        verify(gen).writeNull();
    }

    @Test
    void goalPercentageSerializer_Zero() throws Exception {
        GoalPercentageSerializer serializer = new GoalPercentageSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(BigDecimal.ZERO, gen, context);

        verify(gen).writeNumber(new BigDecimal("0.0"));
    }

    @Test
    void goalPercentageSerializer_OneDecimal() throws Exception {
        GoalPercentageSerializer serializer = new GoalPercentageSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(new BigDecimal("50.0"), gen, context);

        verify(gen).writeNumber(new BigDecimal("50.0"));
    }

    @Test
    void goalPercentageSerializer_TwoDecimals() throws Exception {
        GoalPercentageSerializer serializer = new GoalPercentageSerializer();
        JsonGenerator gen = mock(JsonGenerator.class);
        SerializationContext context = mock(SerializationContext.class);

        serializer.serialize(new BigDecimal("60.33"), gen, context);

        verify(gen).writeNumber(new BigDecimal("60.33"));
    }

    @Test
    void authenticationEntryPoint_ReturnsUnauthorizedResponse()
            throws Exception {

        CustomUserDetailsService userDetailsService =
                mock(CustomUserDetailsService.class);

        SecurityConfig securityConfig =
                new SecurityConfig(userDetailsService);

        org.springframework.security.web.AuthenticationEntryPoint entryPoint =
                securityConfig.authenticationEntryPoint();

        jakarta.servlet.http.HttpServletRequest request =
                mock(jakarta.servlet.http.HttpServletRequest.class);

        jakarta.servlet.http.HttpServletResponse response =
                mock(jakarta.servlet.http.HttpServletResponse.class);

        java.io.PrintWriter writer =
                mock(java.io.PrintWriter.class);

        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(
                request,
                response,
                new org.springframework.security.core.AuthenticationException(
                        "Unauthorized"
                ) {}
        );

        verify(response)
                .setStatus(HttpStatus.UNAUTHORIZED.value());

        verify(response)
                .setContentType("application/json");

        verify(writer)
                .write(contains("\"error\": \"Unauthorized\""));

        verify(writer)
                .write(contains("\"message\": \"Authentication is required\""));
    }


}
