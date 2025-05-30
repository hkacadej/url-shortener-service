package com.henrikacadej.urlshortener.dto;

import java.time.LocalDateTime;

public record ShortUrlResponse(String shortUrl, LocalDateTime expirationDate) {
}
