package com.henrikacadej.urlshortener.repository;

import com.henrikacadej.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    public Optional<Url> findByOriginalUrl(String originalUrl);
}
