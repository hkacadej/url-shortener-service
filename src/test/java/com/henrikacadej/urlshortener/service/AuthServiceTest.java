package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.AuthenticationRequest;
import com.henrikacadej.urlshortener.dto.AuthenticationResponse;
import com.henrikacadej.urlshortener.dto.RegisterRequest;
import com.henrikacadej.urlshortener.entity.User;
import com.henrikacadej.urlshortener.exception.AuthenticationException;
import com.henrikacadej.urlshortener.repository.UserRepository;
import com.henrikacadej.urlshortener.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_successfulRegistration_returnsAuthResponse() {
        RegisterRequest request = new RegisterRequest("Henri", "henri@test.com", "securePass");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(jwtUtil.createToken(request.email())).thenReturn("mockedToken");

        AuthenticationResponse response = authService.register(request);

        verify(userRepository).save(any(User.class));
        assertEquals("mockedToken", response.accessToken());
    }

    @Test
    void register_existingEmail_throwsAuthenticationException() {
        RegisterRequest request = new RegisterRequest("Henri", "henri@test.com", "securePass");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_validCredentials_returnsAuthResponse() {
        AuthenticationRequest request = new AuthenticationRequest("henri@test.com", "securePass");
        User mockUser = User.builder()
                .email("henri@test.com")
                .password("encodedPass")
                .build();

        when(userRepository.findById(request.email())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.createToken(request.email())).thenReturn("mockedToken");

        AuthenticationResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("mockedToken", response.accessToken());
    }

    @Test
    void authenticate_userNotFound_throwsUsernameNotFoundException() {
        AuthenticationRequest request = new AuthenticationRequest("notfound@test.com", "somepass");

        when(userRepository.findById(request.email())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(request));
    }
}
