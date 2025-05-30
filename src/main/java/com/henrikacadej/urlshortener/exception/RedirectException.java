package com.henrikacadej.urlshortener.exception;

public class RedirectException extends RuntimeException {
    public RedirectException(String message, Throwable cause) {
        super(message, cause);
    }
}
