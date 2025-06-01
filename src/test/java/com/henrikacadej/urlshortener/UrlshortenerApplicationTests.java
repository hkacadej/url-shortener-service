package com.henrikacadej.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "test-topic" })
@ActiveProfiles("test")
class UrlshortenerApplicationTests {

    @Test
    void contextLoads() {
    }

}