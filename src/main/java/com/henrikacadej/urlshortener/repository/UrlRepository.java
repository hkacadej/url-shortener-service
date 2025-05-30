package com.henrikacadej.urlshortener.repository;

import com.henrikacadej.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    public Optional<Url> findByOriginalUrlAndExpirationTimeAfter(String originalUrl, LocalDateTime expirationTime);
}
