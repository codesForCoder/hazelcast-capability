package com.aniket.movie.config;

import com.aniket.movie.util.ApplicationUtils;
import com.configcat.ConfigCatClient;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.aniket.movie.constant.CacheContext.CUSTOMER;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private ConfigCatClient client;

    @KafkaListener(topics = "#{'${io.confluent.developer.config.topic.name}'}")
    public void consume(final ConsumerRecord<String, String> consumerRecord) throws InterruptedException {
        log.info("received ---> {} At Env - {}", consumerRecord.value(),applicationUtils.getCurrentEnv());
        KafkaMessage kafkaMessage = new Gson().fromJson(consumerRecord.value(),KafkaMessage.class);
        boolean application_delay_introduced = client.getValue(Boolean.class, "application_delay_introduced", false);
        if(application_delay_introduced)
        {
            Thread.sleep(5000);
        }
        boolean is_datachange_emit_events = client.getValue(Boolean.class, "is_datachange_emit_events", false);
        if(is_datachange_emit_events)
        {
            applicationUtils.evictSingleCacheValue(kafkaMessage.getCacheKeyName() , "cache-data");
        }

    }
}
