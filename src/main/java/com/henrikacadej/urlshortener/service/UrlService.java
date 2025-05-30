package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.entity.Url;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import com.henrikacadej.urlshortener.kafka.producer.KafkaProducerService;
import com.henrikacadej.urlshortener.repository.UrlRepository;
import com.henrikacadej.urlshortener.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UrlService {

    private final UrlRepository urlRepository;
    private final KafkaProducerService kafkaProducerService;

    @Value("${url.expiration.minutes}")
    private long defaultExpirationMinutes;

    public UrlService(UrlRepository urlRepository, KafkaProducerService kafkaProducerService) {
        this.urlRepository = urlRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public ShortUrlResponse getShortUrl(ShortUrlRequest request){
        Url shortened = shortenUrl(request.shortUrl());
        return new ShortUrlResponse(shortened.getShortUrl(),shortened.getExpirationTime());
    }

    private Url shortenUrl(String originalUrl) {
        return urlRepository
                .findByOriginalUrl(originalUrl)
                .map(this::renewUrl).orElseGet(() -> createUrl(originalUrl));
    }

    String generateShortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    Url renewUrl(Url url) {
        url.setExpirationTime(LocalDateTime.now().plusMinutes(defaultExpirationMinutes));
        log.info("Renewing url: {}", url);
        return urlRepository.save(url);
    }

    Url createUrl(String originalUrl){

        Url url = Url.builder()
                .expirationTime(LocalDateTime.now().plusMinutes(defaultExpirationMinutes))
                .shortUrl(generateShortCode())
                .originalUrl(originalUrl)
                .clickCount(0L)
                .build();

        log.info("Created url: {}", url);
        return urlRepository.save(url);
    }

    @Transactional
    public Url saveUrl(Url url){
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
                                log.info("Url expired for shortCode: {} and Url {} ", shortCode, url);
                                throw new UrlNotFoundException("Requested Url is expired");
                            }
                            kafkaProducerService.send(url.getShortUrl());
                            log.info("Returning Url: {}", url);
                            return url.getOriginalUrl();
                        }
                )
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
    }

    boolean isUrlExpired(Url url){
        return url.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Transactional
    public void incrementClickCount(String shortUrl) {
        urlRepository.incrementCounter(shortUrl);
    }
}