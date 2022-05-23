package com.aniket.movie.util;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApplicationUtils {

	  @Autowired
	  private RestTemplate restTemplate;

	@Autowired
	private CacheManager cacheManager;

	@Value("${server.port}")
	private Integer applicationPort;

	public void evictSingleCacheValue(String cacheKey, String cacheName) {
		 cacheManager.getCache(cacheName).evict(cacheKey);
		log.info("CacheName : {} === Data Evicted for Key ----- {}",cacheName,cacheKey);
	}
	@Cacheable(value ="cache-data",key="{#context}+'-'+#key")
	public String getCache(String key , String context) {
		log.info("ACTUAL CACHE GET CALLED TO DIST CACHE -  {} ---->{}",context,key);
		String result = null;
		try {
		result =  restTemplate.exchange("http://localhost:"+applicationPort+"/cache/"+context+"/"+key, HttpMethod.GET, null, String.class).getBody();
		}catch (Exception e) {
			log.error("Exception Happened ----- {}" , e);
		}
		log.info("Cache Context - {} , Key - {} , value - {}" , context , key , result);
		return result;
	}


	public void putCache(String key , String context , String newValue) {
		log.info("CACHE PUT CALLED TO DIST CACHE -  {} ---->{}",context,key);
		boolean result = false;
		try {
			  HttpHeaders headers = new HttpHeaders();
		      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		      HttpEntity<String> entity = new HttpEntity<String>(newValue,headers);
		       restTemplate.exchange("http://localhost:"+applicationPort+"/cache/"+context+"/"+key, HttpMethod.PUT, entity, String.class).getBody();
		       result = true;
		}catch (Exception e) {
			log.error("Exception Happened ----- {}" , e);
		}
		log.info("Cache Context - {} , Key - {} , Updated value - {}" , context , key , newValue);
		log.info("Cache Insert/Update Status ----- {}" , result);
		
	}
}
