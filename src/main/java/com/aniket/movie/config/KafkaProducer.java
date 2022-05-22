package com.aniket.movie.config;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> producer;
    private final NewTopic topic;

    public void produce(KafkaMessage messagePayload) {
        // Produce sample data
            log.info("Producing record:  --> {}", messagePayload);
            producer.send(topic.name(), new Gson().toJson(messagePayload)).addCallback(
                    result -> {
                        final RecordMetadata recordMetadata;
                        if (result != null) {
                            recordMetadata = result.getRecordMetadata();
                            log.info("Produced record to topic {} partition {} @ offset {}",
                                    recordMetadata.topic(),
                                    recordMetadata.partition(),
                                    recordMetadata.offset());
                        }
                    },
                    exception -> log.error("Failed to produce to kafka", exception));


        producer.flush();

        log.info("message were produced to topic {}", topic.name());

    }
}
