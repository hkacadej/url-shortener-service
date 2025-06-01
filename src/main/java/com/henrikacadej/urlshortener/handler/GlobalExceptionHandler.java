package com.henrikacadej.urlshortener.handler;

import com.henrikacadej.urlshortener.exception.AuthenticationException;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public record ErrorResponse(String error, String details, int statusCode, String timestamp) {}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UrlNotFoundException.class)
    public ErrorResponse handleUrlNotFound(UrlNotFoundException ex) {
        log.error("UrlNotFoundException: {}", ex.getMessage());
        return new ErrorResponse(
                "Resource Not Found",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now().toString()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " +
                        Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value"))
                .collect(Collectors.joining("; "));

        return new ErrorResponse(
                "Validation Error",
                details,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthError(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return new ErrorResponse(
                "Authentication Failed",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnexpectedRollbackException.class)
    public ErrorResponse handleTransactionRollback(UnexpectedRollbackException ex) {
        log.error("Transaction rollback error: ", ex);
        return new ErrorResponse(
                "Transaction Error",
                "The current request could not be completed due to a transaction rollback. Please try again.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().toString()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleUnexpected(Exception ex) {
        log.error("Unhandled exception caught: ", ex);
        return new ErrorResponse(
                "Internal Server Error",
                "An unexpected error occurred : " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().toString()
        );
    }
}
