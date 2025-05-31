package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "Endpoints for shortening URLs")
public class UrlShortenerController {

    private final UrlService urlService;

    @Operation(
            summary = "Resolve short URL",
            description = "Resolves a shortened URL ID to its original URL",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resolved successfully",
                            content = @Content(schema = @Schema(implementation = UrlResponse.class))),
                    @ApiResponse(responseCode = "404", description = "URL not found",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Short URL not found\"}")))
            }
    )
    @PostMapping("/shorten")
    public ShortUrlResponse shorten(@Valid @RequestBody ShortUrlRequest request) {
        return urlService.getShortUrl(request);
    }

    @Operation(
            summary = "Get all shortened URLs",
            description = "Returns a list of all shortened URLs for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of URLs",
                            content = @Content(schema = @Schema(implementation = UrlResponse.class)))
            }
    )
    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getUrlList(){
        return ResponseEntity.ok(urlService.getUrlList());
    }
}
