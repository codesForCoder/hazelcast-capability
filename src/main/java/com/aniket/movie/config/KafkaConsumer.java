package com.aniket.movie.config;

import com.aniket.movie.util.ApplicationUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.aniket.movie.constant.CacheContext.CUSTOMER;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    private ApplicationUtils applicationUtils;
    @KafkaListener(topics = "#{'${io.confluent.developer.config.topic.name}'}")
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
        log.info("received ---> {}", consumerRecord.value());
        KafkaMessage kafkaMessage = new Gson().fromJson(consumerRecord.value(),KafkaMessage.class);
        applicationUtils.evictSingleCacheValue(kafkaMessage.getCacheKeyName() , "cache-data");

    }
}
