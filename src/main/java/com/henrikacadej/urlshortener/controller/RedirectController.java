package com.henrikacadej.urlshortener.controller;

import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/r")
@Tag(name = "Redirect Controller", description = "Endpoints for resolving URLs")
public class RedirectController {

    private final UrlService urlService;

    @Operation(
            summary = "Shorten a URL",
            description = "Accepts a long URL and returns a shortened version",
            responses = {
                    @ApiResponse(responseCode = "200", description = "URL shortened successfully",
                            content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid URL",
                            content = @Content(schema = @Schema(example = "{\"url\": \"must not be blank\"}")))
            }
    )
    @GetMapping({"/{id}"})
    public ResponseEntity<UrlResponse> redirect(@PathVariable String id) {
        return ResponseEntity.ok(urlService.getUrl(id));
    }


}
