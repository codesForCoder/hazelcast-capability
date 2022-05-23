package com.aniket.movie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.configcat.*;

@Configuration
public class AppConfig {

    @Value("${config.cat.sdk.key}")
    private String configCatSdk;

    @Value("${config.cat.polling.interval}")
    private Integer pollingInterval;

    @Bean
    public ConfigCatClient configCatClient() {
        ConfigCatClient client = ConfigCatClient.newBuilder()
                .mode(PollingModes.autoPoll(pollingInterval /* polling interval in seconds */))
                .logLevel(LogLevel.INFO) // <-- Set the log level to INFO to track how your feature flags were evaluated. When moving to production, you can remove this line to avoid too detailed logging.
                .build(configCatSdk); // <-- This is the actual SDK Key for your 'Test Environment' environment.
        return client;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("cache-data");
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
