package com.henrikacadej.urlshortener.kafka.consumer;

import com.henrikacadej.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final UrlService urlService;

    @KafkaListener(topics = "kraft-topic", groupId = "kraft-group")
    public void consume(String message) {
        log.info("Incrementing click count for url Id : {}", message);
        urlService.incrementClickCount(message);
    }
}

