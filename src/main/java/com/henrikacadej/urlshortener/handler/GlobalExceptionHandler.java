package com.henrikacadej.urlshortener.handler;


import com.henrikacadej.urlshortener.exception.RedirectException;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UrlNotFoundException.class)
    public Map<String, String> handleUrlNotFound(UrlNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RedirectException.class)
    public Map<String, String> handleRedirectError(RedirectException ex) {
        log.error(ex.getMessage());
        return Map.of("error", "Internal redirect error");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleGenericError(Exception ex) {
        log.error(ex.getMessage());
        return Map.of("error", "An unexpected error occurred");
    }
}

