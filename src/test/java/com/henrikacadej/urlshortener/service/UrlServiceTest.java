package com.henrikacadej.urlshortener.service;

import com.henrikacadej.urlshortener.dto.ShortUrlRequest;
import com.henrikacadej.urlshortener.dto.ShortUrlResponse;
import com.henrikacadej.urlshortener.dto.UrlResponse;
import com.henrikacadej.urlshortener.entity.Url;
import com.henrikacadej.urlshortener.exception.UrlNotFoundException;
import com.henrikacadej.urlshortener.kafka.producer.KafkaProducerService;
import com.henrikacadej.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String ORIGINAL_URL = "https://example.com";
    private static final String SHORT_CODE = "abc12345";
    private static final long DEFAULT_EXPIRATION_MINUTES = 60L;
    private static final String URL_ORIGIN = "https://my-origin.com";
    private static final String URL_ENDPOINT = "/r/";
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "defaultExpirationMinutes", DEFAULT_EXPIRATION_MINUTES);
        ReflectionTestUtils.setField(urlService, "urlOrigin", URL_ORIGIN);
        ReflectionTestUtils.setField(urlService, "urlEndpoint", URL_ENDPOINT);

    }

    @Test
    void getShortUrl_WhenUrlExists_ShouldRenewAndReturnExistingUrl() {
        // Arrange
        ShortUrlRequest request = new ShortUrlRequest(ORIGINAL_URL);
        Url existingUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(30));
        Url renewedUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES));

        when(urlRepository.findByOriginalUrl(ORIGINAL_URL)).thenReturn(Optional.of(existingUrl));
        when(urlService.saveUrl(any(Url.class))).thenReturn(renewedUrl);

        // Act
        ShortUrlResponse response = urlService.getShortUrl(request);

        // Assert
        assertNotNull(response);
        assertEquals(URL_ORIGIN + URL_ENDPOINT + SHORT_CODE, response.shortUrl());
        assertEquals(renewedUrl.getExpirationTime(), response.expirationDate());
        verify(urlRepository).findByOriginalUrl(ORIGINAL_URL);
        verify(urlRepository).save(existingUrl);
    }

    @Test
    void getShortUrl_WhenUrlDoesNotExist_ShouldCreateNewUrl() {
        // Arrange
        ShortUrlRequest request = new ShortUrlRequest(ORIGINAL_URL);
        Url newUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES));

        when(urlRepository.findByOriginalUrl(ORIGINAL_URL)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(newUrl);

        // Act
        ShortUrlResponse response = urlService.getShortUrl(request);

        // Assert
        assertNotNull(response);
        assertEquals(URL_ORIGIN + URL_ENDPOINT + SHORT_CODE, response.shortUrl());
        assertEquals(newUrl.getExpirationTime(), response.expirationDate());
        verify(urlRepository).findByOriginalUrl(ORIGINAL_URL);
        verify(urlRepository).save(any(Url.class));
    }


    @Test
    void getUrl2_WhenUrlExistsAndNotExpired_ShouldReturnOriginalUrl() {
        // Arrange
        Url url = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(30));
        when(urlRepository.findById(SHORT_CODE)).thenReturn(Optional.of(url));

        // Act
        UrlResponse result = urlService.getUrl(SHORT_CODE);

        // Assert
        assertEquals(ORIGINAL_URL, result.originalUrl());
        verify(urlRepository).findById(SHORT_CODE);
    }

    @Test
    void getUrl_WhenUrlExistsButExpired_ShouldThrowUrlNotFoundException() {
        // Arrange
        Url expiredUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().minusMinutes(30));
        when(urlRepository.findById(SHORT_CODE)).thenReturn(Optional.of(expiredUrl));

        // Act & Assert
        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getUrl(SHORT_CODE)
        );
        assertEquals("Requested Url is expired", exception.getMessage());
        verify(urlRepository).findById(SHORT_CODE);
    }

    @Test
    void getUrl_WhenUrlDoesNotExist_ShouldThrowUrlNotFoundException() {
        // Arrange
        when(urlRepository.findById(SHORT_CODE)).thenReturn(Optional.empty());

        // Act & Assert
        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getUrl(SHORT_CODE)
        );
        assertEquals("Short URL not found", exception.getMessage());
        verify(urlRepository).findById(SHORT_CODE);
    }

    @Test
    void renewUrl_ShouldUpdateExpirationTimeAndSave() {
        // Arrange
        Url url = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(10));
        LocalDateTime beforeRenewal = url.getExpirationTime();

        when(urlRepository.save(url)).thenReturn(url);

        // Act
        Url result = urlService.renewUrl(url);

        // Assert
        assertNotNull(result);
        assertTrue(result.getExpirationTime().isAfter(beforeRenewal));
        verify(urlRepository).save(url);
    }

    @Test
    void createUrl_ShouldCreateNewUrlWithCorrectProperties() {
        // Arrange
        Url newUrl = createUrlEntity("newCode", ORIGINAL_URL, LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES));
        when(urlRepository.save(any())).thenReturn(newUrl);

        // Act
        Url result = urlService.createUrl(ORIGINAL_URL);

        // Assert
        assertNotNull(result);
        assertEquals(ORIGINAL_URL, result.getOriginalUrl());
        assertEquals("newCode", result.getShortUrl());
        assertEquals(0L, result.getClickCount());
        assertNotNull(result.getExpirationTime());
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void isUrlExpired_WhenUrlIsExpired_ShouldReturnTrue() {
        // Arrange
        Url expiredUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().minusMinutes(30));

        // Act
        boolean result = urlService.isUrlExpired(expiredUrl);

        // Assert
        assertTrue(result);
    }

    @Test
    void isUrlExpired_WhenUrlIsNotExpired_ShouldReturnFalse() {
        // Arrange
        Url activeUrl = createUrlEntity(SHORT_CODE, ORIGINAL_URL, LocalDateTime.now().plusMinutes(30));

        // Act
        boolean result = urlService.isUrlExpired(activeUrl);

        // Assert
        assertFalse(result);
    }

    @Test
    void generateShortCode_ShouldReturnEightCharacterString() {
        // Act
        String shortCode = urlService.generateShortCode();

        // Assert
        assertNotNull(shortCode);
        assertEquals(8, shortCode.length());
    }

    @Test
    void getShortUrl_WithNullRequest_ShouldHandleGracefully() {
        // Arrange
        ShortUrlRequest request = new ShortUrlRequest(null);

        when(urlRepository.findByOriginalUrl(null)).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        assertDoesNotThrow(() -> urlService.getShortUrl(request));
    }

    @Test
    void getShortUrl_WithEmptyString_ShouldHandleGracefully() {
        // Arrange
        ShortUrlRequest request = new ShortUrlRequest("");

        when(urlRepository.findByOriginalUrl("")).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        assertDoesNotThrow(() -> urlService.getShortUrl(request));
    }

    // Helper method to create Url entities for testing
    private Url createUrlEntity(String shortCode, String originalUrl, LocalDateTime expirationTime) {
        return Url.builder()
                .shortUrl(shortCode)
                .originalUrl(originalUrl)
                .expirationTime(expirationTime)
                .clickCount(0L)
                .build();
    }

    @Test
    void incrementClickCount_callsRepository() {
        String shortUrl = "/abc123";

        urlService.incrementClickCount(shortUrl);

        verify(urlRepository).incrementCounter(shortUrl);
    }

    @Test
    void getUrlList_returnsMappedResponseList() {
        // Prepare dummy Url entities
        Url url1 = new Url();
        url1.setShortUrl("/abc123");
        url1.setOriginalUrl("https://example.com/1");
        url1.setClickCount(5L);

        Url url2 = new Url();
        url2.setShortUrl("/def456");
        url2.setOriginalUrl("https://example.com/2");
        url2.setClickCount(10L);

        List<Url> urls = List.of(url1, url2);

        // Mock repository method
        when(urlRepository.findAllByExpirationTimeAfter(any(LocalDateTime.class))).thenReturn(urls);

        // Call service method
        List<UrlResponse> responses = urlService.getUrlList();

        // Verify repository interaction
        verify(urlRepository).findAllByExpirationTimeAfter(any(LocalDateTime.class));

        // Assert mapped results
        assertEquals(2, responses.size());

        assertEquals("https://my-origin", responses.get(0).shortUrl().substring(0, 17));
        assertEquals("https://example.com/1", responses.get(0).originalUrl());
        assertEquals(5L, responses.get(0).clickCount());

        assertEquals("https://my-origin", responses.get(1).shortUrl().substring(0, 17));
        assertEquals("https://example.com/2", responses.get(1).originalUrl());
        assertEquals(10L, responses.get(1).clickCount());
    }
}
