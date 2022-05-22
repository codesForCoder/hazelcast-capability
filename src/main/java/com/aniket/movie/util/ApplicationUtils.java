package com.aniket.movie.util;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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


	@Cacheable(value ="cache-data",key="{#key, #context}")
	public String getCache(String key , String context) {
		log.info("ACTUAL CACHE GET CALLED TO DIST CACHE -  {} ---->{}",context,key);
		String result = null;
		try {
		result =  restTemplate.exchange("http://localhost:8080/cache/"+context+"/"+key, HttpMethod.GET, null, String.class).getBody();
		}catch (Exception e) {
			log.error("Exception Happened ----- {}" , e);
		}
		log.info("Cache Context - {} , Key - {} , value - {}" , context , key , result);
		return result;
	}

	@CacheEvict(value="cache-data",key="{#key, #context}")
	public void putCache(String key , String context , String newValue) {
		log.info("CACHE PUT CALLED TO DIST CACHE -  {} ---->{}",context,key);
		boolean result = false;
		try {
			  HttpHeaders headers = new HttpHeaders();
		      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		      HttpEntity<String> entity = new HttpEntity<String>(newValue,headers);
		       restTemplate.exchange("http://localhost:8080/cache/"+context+"/"+key, HttpMethod.PUT, entity, String.class).getBody();
		       result = true;
		}catch (Exception e) {
			log.error("Exception Happened ----- {}" , e);
		}
		log.info("Cache Context - {} , Key - {} , Updated value - {}" , context , key , newValue);
		log.info("Cache Insert/Update Status ----- {}" , result);
		
	}
}
