package com.henrikacadej.urlshortener.handler;


import com.henrikacadej.urlshortener.exception.RedirectException;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public void handleUrlNotFound(UrlNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RedirectException.class)
    public void handleRedirectError(RedirectException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal redirect error");
    }

    @ExceptionHandler(Exception.class)
    public void handleGenericError(Exception ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
