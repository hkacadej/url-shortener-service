package com.henrikacadej.urlshortener.util;

import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.entity.Url;

import java.util.List;

public class UrlMapper {

    public static UrlResponse toResponse(Url url, String host) {
        return new UrlResponse(
                host + url.getShortUrl(),
                url.getOriginalUrl(),
                url.getClickCount()
        );
    }

    public static List<UrlResponse> toResponseList(List<Url> urls, String host) {
        return urls.stream()
                .map(url -> toResponse(url, host))
                .toList();
    }
}

