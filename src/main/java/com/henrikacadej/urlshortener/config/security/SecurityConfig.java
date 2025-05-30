package com.henrikacadej.urlshortener.config.security;

import com.henrikacadej.urlshortener.security.entry.CustomAuthEntryPoint;
import com.henrikacadej.urlshortener.security.filter.JwtAuthenticationFilter;
import com.henrikacadej.urlshortener.security.oauth2.SuccessHandler;
import com.henrikacadej.urlshortener.service.UserDetailsService;
import com.henrikacadej.urlshortener.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    @Order(1)
    public SecurityFilterChain oauth2LoginSecurityChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login/**", "/oauth2/**", "/auth/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler())
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthEntryPoint())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    SuccessHandler oAuth2LoginSuccessHandler() {
        return new SuccessHandler(jwtUtil, userDetailsService);
    }

}
