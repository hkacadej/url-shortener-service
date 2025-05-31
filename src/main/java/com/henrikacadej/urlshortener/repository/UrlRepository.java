package com.henrikacadej.urlshortener.repository;

import com.henrikacadej.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, String> {
    Optional<Url> findByOriginalUrl(String originalUrl);

    @Modifying
    @Query("UPDATE Url e SET e.clickCount = e.clickCount + 1 WHERE e.shortUrl = :shortUrl")
    void incrementCounter(@Param("shortUrl") String shortUrl);

    List<Url> findAllByExpirationTimeAfter(LocalDateTime now);

    @Query(value = "SELECT * FROM url WHERE expiration_time > NOW()", nativeQuery = true)
    List<Url> findAllNotExpiredNative();
}
