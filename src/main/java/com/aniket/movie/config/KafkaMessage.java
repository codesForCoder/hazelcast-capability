package com.aniket.movie.config;

import lombok.Data;

@Data
public class KafkaMessage {
private String cacheKeyName;
private String context;
}
