package com.henrikacadej.urlshortener.util;

import static org.junit.jupiter.api.Assertions.*;

import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.entity.Url;
import org.junit.jupiter.api.Test;

import java.util.List;

class UrlMapperTest {

    @Test
    void testToResponse() {
        Url url = new Url();
        url.setShortUrl("/abc123");
        url.setOriginalUrl("https://example.com/long-url");
        url.setClickCount(42L);

        String host = "https://short.url";

        UrlResponse response = UrlMapper.toResponse(url, host);

        assertEquals("https://short.url/abc123", response.shortUrl());
        assertEquals("https://example.com/long-url", response.originalUrl());
        assertEquals(42, response.clickCount());
    }

    @Test
    void testToResponseList() {
        Url url1 = new Url();
        url1.setShortUrl("/abc123");
        url1.setOriginalUrl("https://example.com/1");
        url1.setClickCount(10L);

        Url url2 = new Url();
        url2.setShortUrl("/def456");
        url2.setOriginalUrl("https://example.com/2");
        url2.setClickCount(20L);

        List<Url> urls = List.of(url1, url2);
        String host = "https://short.url";

        List<UrlResponse> responses = UrlMapper.toResponseList(urls, host);

        assertEquals(2, responses.size());

        assertEquals("https://short.url/abc123", responses.get(0).shortUrl());
        assertEquals("https://example.com/1", responses.get(0).originalUrl());
        assertEquals(10L, responses.get(0).clickCount());

        assertEquals("https://short.url/def456", responses.get(1).shortUrl());
        assertEquals("https://example.com/2", responses.get(1).originalUrl());
        assertEquals(20L, responses.get(1).clickCount());
    }
}

