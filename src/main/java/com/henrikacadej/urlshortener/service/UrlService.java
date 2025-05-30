package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.entity.Url;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import com.henrikacadej.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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
                .findByOriginalUrl(originalUrl)
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

        Url url = Url.builder()
                .expirationTime(LocalDateTime.now().plusMinutes(defaultExpirationMinutes))
                .shortCode(generateShortCode())
                .originalUrl(originalUrl)
                .clickCount(0L)
                .build();

        return urlRepository.save(url);

    }

    public Optional<Url> getUrl(String shortCode){
        return urlRepository.findById(shortCode);
    }

    public String getUrl2(String shortCode){
        return getUrl(shortCode)
                .map(
                        url -> {
                            if (isUrlExpired(url)){
                                throw new UrlNotFoundException("Requested Url is expired");
                            }
                            return url.getOriginalUrl();
                        }
                )
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
    }

    private boolean isUrlExpired(Url url){
        return url.getExpirationTime().isBefore(LocalDateTime.now());
    }
}