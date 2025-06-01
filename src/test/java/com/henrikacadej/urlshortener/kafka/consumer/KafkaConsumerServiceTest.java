package com.henrikacadej.urlshortener.kafka.consumer;

import com.henrikacadej.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KafkaConsumerServiceTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void consume_ShouldCallUrlServiceIncrementClickCount_WhenMessageReceived() {
        // Given
        String testMessage = "test-url-id-123";

        // When
        kafkaConsumerService.consume(testMessage);

        // Then
        verify(urlService, times(1)).incrementClickCount(eq(testMessage));
    }

    @Test
    void consume_ShouldHandleNullMessage_WithoutThrowingException() {
        // Given
        String nullMessage = null;

        kafkaConsumerService.consume(nullMessage);
        verify(urlService, times(1)).incrementClickCount(eq(nullMessage));
    }

    @Test
    void consume_ShouldHandleEmptyMessage_WithoutThrowingException() {
        // Given
        String emptyMessage = "";

        // When
        kafkaConsumerService.consume(emptyMessage);

        // Then
        verify(urlService, times(1)).incrementClickCount(eq(emptyMessage));
    }

    @Test
    void consume_ShouldPropagateExceptionFromUrlService() {
        // Given
        String testMessage = "test-url-id";
        RuntimeException expectedException = new RuntimeException("Database error");
        doThrow(expectedException).when(urlService).incrementClickCount(testMessage);

        // When & Then
        try {
            kafkaConsumerService.consume(testMessage);
        } catch (RuntimeException e) {
            // Exception should be propagated
            assert e.getMessage().equals("Database error");
        }

        verify(urlService, times(1)).incrementClickCount(eq(testMessage));
    }
}