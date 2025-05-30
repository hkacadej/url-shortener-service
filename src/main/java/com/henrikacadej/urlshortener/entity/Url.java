package com.henrikacadej.urlshortener.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class Url {

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Id
    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortUrl;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;
}