package com.henrikacadej.urlshortener.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrikacadej.urlshortener.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import com.henrikacadej.urlshortener.service.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String jwtToken = jwtUtil.createToken(email);
        
        // TODO make async
        userRegistration(oAuth2User);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("email", email);

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), responseBody);

    }

    private void userRegistration(OAuth2User oAuth2User) {
        userDetailsService.handleUserLogin(oAuth2User);
    }
}
