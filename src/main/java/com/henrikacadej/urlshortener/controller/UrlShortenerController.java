package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ShortUrlResponse shorten(@RequestBody ShortUrlRequest request) {
        return urlService.getShortUrl(request);
    }
}
