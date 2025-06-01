package com.henrikacadej.urlshortener.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.handler.GlobalExceptionHandler;
import com.henrikacadej.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shorten_ShouldReturnShortUrlResponse_WhenValidRequest() throws Exception {
        // Given
        ShortUrlRequest request = new ShortUrlRequest("https://example.com/very-long-url");

        LocalDateTime now = LocalDateTime.now();

        ShortUrlResponse response = new ShortUrlResponse("http://short.ly/abc123", now);

        when(urlService.getShortUrl(any(ShortUrlRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://short.ly/abc123"));

        verify(urlService, times(1)).getShortUrl(any(ShortUrlRequest.class));
    }

    @Test
    void shorten_ShouldReturnBadRequest_WhenInvalidUrl() throws Exception {
        // Given
        ShortUrlRequest request = new ShortUrlRequest("");

        // When & Then
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(urlService, never()).getShortUrl(any(ShortUrlRequest.class));
    }

    @Test
    void shorten_ShouldReturnBadRequest_WhenMalformedUrl() throws Exception {
        // Given
        ShortUrlRequest request = new ShortUrlRequest("not-a-valid-url");

        // When & Then
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getUrlList_ShouldReturnListOfUrls_WhenCalled() throws Exception {
        // Given
        UrlResponse url1 = new UrlResponse("abc123", "https://example.com", 0L);

        UrlResponse url2 = new UrlResponse("def456", "https://google.com", 0L);

        List<UrlResponse> urlList = Arrays.asList(url1, url2);

        when(urlService.getUrlList()).thenReturn(urlList);

        // When & Then
        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].shortUrl").value("abc123"))
                .andExpect(jsonPath("$[0].originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$[0].clickCount").value(0L))
                .andExpect(jsonPath("$[1].shortUrl").value("def456"))
                .andExpect(jsonPath("$[1].originalUrl").value("https://google.com"))
                .andExpect(jsonPath("$[1].clickCount").value(0L));

        verify(urlService, times(1)).getUrlList();
    }

    @Test
    void getUrlList_ShouldReturnEmptyList_WhenNoUrls() throws Exception {
        // Given
        when(urlService.getUrlList()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(urlService, times(1)).getUrlList();
    }

    @Test
    void shorten_ShouldReturnInternalServerError_WhenException() throws Exception {
        // Given
        ShortUrlRequest request = new ShortUrlRequest("https://example.com/very-long-url");

        // When & Then

        when(urlService.getShortUrl(any(ShortUrlRequest.class))).thenThrow(new RuntimeException("Exception while shortening URL"));

        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.details").value("An unexpected error occurred : Exception while shortening URL"));

    }
}
