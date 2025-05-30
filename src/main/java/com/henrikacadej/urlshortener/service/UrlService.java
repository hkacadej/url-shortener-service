package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.entity.Url;
import com.henrikacadej.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Value("${url.expiration.minutes}")
    private long defaultExpirationMinutes;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ShortUrlResponse getShortUrl(ShortUrlRequest request){
        Url short2 = shortenUrl(request.shortUrl());
        return new ShortUrlResponse(short2.getShortCode(),short2.getExpirationTime());
    }

    private Url shortenUrl(String originalUrl) {
        return urlRepository
                .findByOriginalUrlAndExpirationTimeAfter(originalUrl, LocalDateTime.now())
                .map(this::renewUrl).orElseGet(() -> createUrl(originalUrl));
    }

    private String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Url renewUrl(Url url) {
        url.setExpirationTime(LocalDateTime.now().plusMinutes(defaultExpirationMinutes));
        return urlRepository.save(url);
    }

    private Url createUrl(String originalUrl){
        return Url.builder()
                .expirationTime(LocalDateTime.now().plusMinutes(defaultExpirationMinutes))
                .shortCode(generateShortCode())
                .originalUrl(originalUrl)
                .build();
    }
}