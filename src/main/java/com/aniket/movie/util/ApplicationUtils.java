package com.aniket.movie.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.response.EnvironmentDetails;
import com.aniket.movie.response.SearchListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;

    @Value("${hazelcast.driver.service.url.base}")
    private String hazelcastServiceUrlBase;

    public void evictSingleCacheValue(String cacheKey, String cacheName) {
        cacheManager.getCache(cacheName).evict(cacheKey);
        log.info("CacheName : {} === Data Evicted for Key ----- {}", cacheName, cacheKey);
    }

    @Cacheable(value = "cache-data", key = "{#context}+'-'+#key")
    public String getCache(String key, String context) {
        log.info("ACTUAL CACHE GET CALLED TO DIST CACHE -  {} ---->{}", context, key);
        String result = null;
        try {
            //result =  restTemplate.exchange("http://localhost:"+applicationPort+"/cache/"+context+"/"+key, HttpMethod.GET, null, String.class).getBody();
            result = restTemplate.exchange(hazelcastServiceUrlBase + "/cache/" + context + "/" + key, HttpMethod.GET, null, String.class).getBody();
        } catch (Exception e) {
            log.error("Exception Happened ----- {}", e);
        }
        log.info("Cache Context - {} , Key - {} , value - {}", context, key, result);
        return result;
    }


    public void putCache(String key, String context, String newValue) {
        log.info("CACHE PUT CALLED TO DIST CACHE -  {} ---->{}", context, key);
        boolean result = false;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<String>(newValue, headers);
            // restTemplate.exchange("http://localhost:"+applicationPort+"/cache/"+context+"/"+key, HttpMethod.PUT, entity, String.class).getBody();
            restTemplate.exchange(hazelcastServiceUrlBase + "/cache/" + context + "/" + key, HttpMethod.PUT, entity, String.class).getBody();
            result = true;
        } catch (Exception e) {
            log.error("Exception Happened ----- {}", e);
        }
        log.info("Cache Context - {} , Key - {} , Updated value - {}", context, key, newValue);
        log.info("Cache Insert/Update Status ----- {}", result);

    }

    public SearchListResponse getSearchedResult(SearchListRequest searchListRequest) {
        log.info("Calling Search Service with Payload -   {} ", searchListRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<SearchListRequest> entity = new HttpEntity<SearchListRequest>(searchListRequest, headers);
        SearchListResponse result = restTemplate.exchange("http://localhost:"+applicationPort+  "/cache/search/" + searchListRequest.getContext(), HttpMethod.POST, entity, SearchListResponse.class).getBody();
       // SearchListResponse result = restTemplate.exchange(hazelcastServiceUrlBase + "/cache/search/" + searchListRequest.getContext(), HttpMethod.POST, entity, SearchListResponse.class).getBody();

        log.info("Cache Searched Result ----- {}", result);
        return result;
    }


    public EnvironmentDetails getCurrentEnv() {
        EnvironmentDetails environmentDetails = new EnvironmentDetails();
        try {
            // Port
            environmentDetails.setPostNumber(environment.getProperty("server.port"));
            // Local address
            environmentDetails.setLocalHostAddr(InetAddress.getLocalHost().getHostAddress());
            environmentDetails.setLocalHostName(InetAddress.getLocalHost().getHostName());

            // Remote address
            environmentDetails.setRemoteHostAddr(InetAddress.getLoopbackAddress().getHostAddress());
            environmentDetails.setRemoteHostName(InetAddress.getLoopbackAddress().getHostName());
        } catch (UnknownHostException e) {
            log.error("Error in Retriving Data - {}", e);
        }

        return environmentDetails;

    }
}
