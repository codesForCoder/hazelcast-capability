package com.aniket.movie.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "#{'${io.confluent.developer.config.topic.name}'}")
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
        log.info("received {}  ---> {}", consumerRecord.key(), consumerRecord.value());

    }
}
