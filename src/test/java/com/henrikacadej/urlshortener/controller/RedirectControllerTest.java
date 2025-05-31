package com.henrikacadej.urlshortener.controller;


import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import com.henrikacadej.urlshortener.handler.GlobalExceptionHandler;
import com.henrikacadej.urlshortener.service.UrlService;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RedirectControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private RedirectController redirectController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(redirectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();    }

    @Test
    void redirect_ShouldReturnUrlResponse_WhenValidId() throws Exception {
        // Given
        String shortId = "abc123";
        UrlResponse urlResponse = new UrlResponse(shortId,"https://example.com",0L);

        when(urlService.getUrl(shortId)).thenReturn(urlResponse);

        // When & Then
        mockMvc.perform(get("/r/{id}", shortId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(shortId))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.clickCount").value(0L));

        verify(urlService, times(1)).getUrl(shortId);
    }

    @Test
    void redirect_ShouldHandleEmptyId() throws Exception {
        // When & Then
        mockMvc.perform(get("/r/"))
                .andExpect(status().isInternalServerError());

        verify(urlService, never()).getUrl(anyString());
    }

    @Test
    void redirect_ShouldReturnNotFound_WhenInvalidId() throws Exception {
        // Given
        String shortId = "invalid";

        when(urlService.getUrl(shortId)).thenThrow(new UrlNotFoundException("Url not found"));

        // When & Then
        mockMvc.perform(get("/r/{id}", shortId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Url not found"));

    }

}

