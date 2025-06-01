package com.henrikacadej.urlshortener.kafka.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private CompletableFuture<SendResult<String, String>> sendResultFuture;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void send_ShouldCallKafkaTemplateSendWithCorrectTopicAndMessage() {
        // Given
        String testMessage = "test-message-123";
        String expectedTopic = "kraft-topic";
        when(kafkaTemplate.send(eq(expectedTopic), eq(testMessage))).thenReturn(sendResultFuture);

        // When
        kafkaProducerService.send(testMessage);

        // Then
        verify(kafkaTemplate, times(1)).send(eq(expectedTopic), eq(testMessage));
    }

    @Test
    void send_ShouldHandleNullMessage() {
        // Given
        String nullMessage = null;
        String expectedTopic = "kraft-topic";
        when(kafkaTemplate.send(eq(expectedTopic), isNull())).thenReturn(sendResultFuture);

        // When
        kafkaProducerService.send(nullMessage);

        // Then
        verify(kafkaTemplate, times(1)).send(eq(expectedTopic), eq(nullMessage));
    }

    @Test
    void send_ShouldHandleEmptyMessage() {
        // Given
        String emptyMessage = "";
        String expectedTopic = "kraft-topic";
        when(kafkaTemplate.send(eq(expectedTopic), eq(emptyMessage))).thenReturn(sendResultFuture);

        // When
        kafkaProducerService.send(emptyMessage);

        // Then
        verify(kafkaTemplate, times(1)).send(eq(expectedTopic), eq(emptyMessage));
    }

    @Test
    void send_ShouldHandleLongMessage() {
        // Given
        String longMessage = "a".repeat(1000);
        String expectedTopic = "kraft-topic";
        when(kafkaTemplate.send(eq(expectedTopic), eq(longMessage))).thenReturn(sendResultFuture);

        // When
        kafkaProducerService.send(longMessage);

        // Then
        verify(kafkaTemplate, times(1)).send(eq(expectedTopic), eq(longMessage));
    }

    @Test
    void constructor_ShouldInitializeKafkaTemplate() {
        // Given
        KafkaTemplate<String, String> mockTemplate = mock(KafkaTemplate.class);

        // When
        KafkaProducerService service = new KafkaProducerService(mockTemplate);

        // Then
        KafkaTemplate<String, String> injectedTemplate =
                (KafkaTemplate<String, String>) ReflectionTestUtils.getField(service, "kafkaTemplate");
        assert injectedTemplate == mockTemplate;
    }
}