package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.exception.RedirectException;
import com.henrikacadej.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/r")
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{id}")
    public void redirect(@PathVariable String id, HttpServletResponse response) {

        String originalUrl = urlService.getUrl2(id);

        try {
            response.sendRedirect(originalUrl);
        } catch (IOException e) {
            throw new RedirectException("Failed to redirect to original URL", e);
        }

    }


}
