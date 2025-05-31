package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.AuthenticationRequest;
import com.henrikacadej.urlshortener.dto.AuthenticationResponse;
import com.henrikacadej.urlshortener.dto.RegisterRequest;
import com.henrikacadej.urlshortener.repository.UserRepository;
import com.henrikacadej.urlshortener.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.henrikacadej.urlshortener.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.info("Email address already in use");
            throw new RuntimeException("Email already registered");
        }

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles("USER")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        log.info("Registering user");
        userRepository.save(user);

        var accessToken = jwtUtil.createToken(user.getEmail());

        return new AuthenticationResponse(accessToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findById(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var accessToken = jwtUtil.createToken(user.getEmail());

        log.info("Authenticated user {}", user.getEmail());
        return new AuthenticationResponse(accessToken);
    }

}

