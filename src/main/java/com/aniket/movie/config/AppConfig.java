package com.aniket.movie.config;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Configuration
public class AppConfig {


	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("cache-data");
	}
	  @Bean
	   public RestTemplate getRestTemplate() {
	      return new RestTemplate();
	   }
}
