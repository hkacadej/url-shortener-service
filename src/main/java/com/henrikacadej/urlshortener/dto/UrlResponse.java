package com.henrikacadej.urlshortener.dto;

public record UrlResponse(String shortUrl, String originalUrl, Long clickCount) {

}
