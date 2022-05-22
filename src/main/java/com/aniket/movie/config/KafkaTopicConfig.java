package com.aniket.movie.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    // injected from application.properties
    @Value("${io.confluent.developer.config.topic.name}")
    private String topicName;

    @Value("${io.confluent.developer.config.topic.partitions}")
    private int numPartitions;

    @Value("${io.confluent.developer.config.topic.replicas}")
    private int replicas;

    @Bean
    NewTopic CacheEventTopic() {
        return new NewTopic(topicName, numPartitions, (short) replicas);
    }


}
