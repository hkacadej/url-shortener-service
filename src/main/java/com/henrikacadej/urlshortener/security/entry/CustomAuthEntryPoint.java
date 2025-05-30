package com.henrikacadej.urlshortener.security.entry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        switch (authException.getCause()) {
            case io.jsonwebtoken.ExpiredJwtException expiredJwtException ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
            case io.jsonwebtoken.MalformedJwtException malformedJwtException ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
            case io.jsonwebtoken.security.SignatureException signatureException ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
            case null, default -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Authentication required\"}");
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Authentication required\"}");
    }
}
