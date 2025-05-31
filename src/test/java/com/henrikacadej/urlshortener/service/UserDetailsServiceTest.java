package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.entity.User;
import com.henrikacadej.urlshortener.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // Arrange
        String email = "user@test.com";
        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userRepository.findById(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void loadUserByUsername_userDoesNotExist_throwsException() {
        String email = "notfound@test.com";
        when(userRepository.findById(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(email));
    }
}

