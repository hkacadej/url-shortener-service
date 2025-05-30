package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.exception.RedirectException;
import com.henrikacadej.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/r")
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{id}")
    public ResponseEntity<?> redirect(@PathVariable String id, HttpServletResponse response) {

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getUrl2(id)))
                .build();

    }


}
