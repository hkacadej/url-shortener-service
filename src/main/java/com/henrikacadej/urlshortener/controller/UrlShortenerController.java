package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ShortUrlResponse shorten(@RequestBody ShortUrlRequest request) {
        return urlService.getShortUrl(request);
    }

    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getUrlList(){
        return ResponseEntity.ok(urlService.getUrlList());
    }
}
