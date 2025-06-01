package com.henrikacadej.urlshortener.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "12345678901234567890123456789012";
    private final long expiration = 1000 * 60 * 60;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, secret);

        Field expirationField = JwtUtil.class.getDeclaredField("validityInMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, expiration);
    }

    @Test
    void testCreateAndExtractToken() {
        String email = "henri@example.com";
        String token = jwtUtil.createToken(email);

        assertNotNull(token);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(email, extractedUsername);
    }

    @Test
    void testValidateToken_Success() {
        String email = "henri@example.com";
        String token = jwtUtil.createToken(email);
        UserDetails userDetails = User.builder()
                .username(email)
                .password("pass")
                .roles("USER")
                .build();

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testValidateToken_Failure() {
        String token = jwtUtil.createToken("wrong@example.com");
        UserDetails userDetails = User.builder()
                .username("henri@example.com")
                .password("pass")
                .roles("USER")
                .build();

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testExpiredToken() throws Exception {
        Field expirationField = JwtUtil.class.getDeclaredField("validityInMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, -1);

        String token = jwtUtil.createToken("henri@example.com");

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.extractUsername(token));
    }
}
