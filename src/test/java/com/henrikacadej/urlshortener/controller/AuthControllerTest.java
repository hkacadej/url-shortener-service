package com.henrikacadej.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrikacadej.urlshortener.dto.AuthenticationRequest;
import com.henrikacadej.urlshortener.dto.AuthenticationResponse;
import com.henrikacadej.urlshortener.dto.RegisterRequest;
import com.henrikacadej.urlshortener.exception.AuthenticationException;
import com.henrikacadej.urlshortener.handler.GlobalExceptionHandler;
import com.henrikacadej.urlshortener.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenValidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("test@example.com", "Test User", "password123");

        AuthenticationResponse response = new AuthenticationResponse("jwt-token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));

        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("", "", "");
        // Empty request with validation errors

        // When & Then
        mockMvc.perform(post("/api/auth/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenValidCredentials() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password123");

        AuthenticationResponse response = new AuthenticationResponse("jwt-token");

        when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));

        verify(authService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void authenticate_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("", "");

        // When & Then
        mockMvc.perform(post("/api/auth/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void authenticate_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password123");

        // When & Then

        when(authService.authenticate(any(AuthenticationRequest.class))).thenThrow(new AuthenticationException("Email already registered"));

        mockMvc.perform(post("/api/auth/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Email already registered"));

    }

}
