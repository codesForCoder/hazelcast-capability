package com.aniket.movie.controller;


import com.aniket.movie.config.HazelCastMessageListner;
import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.response.SearchListResponse;
import com.aniket.movie.service.CacheService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aniket.movie.config.HazelCastCacheConfig.IS_LISTENR_ATTACHED_TO_CONTEXT;
import static com.aniket.movie.constant.ApplicationConstant.DASH;
import static com.aniket.movie.constant.RestEndpoint.CACHE_ENDPOINT;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(CACHE_ENDPOINT)
public class HazelCastController {

    @Autowired
    @Qualifier("hazelcastInstance")
    private HazelcastInstance hazelcastInstance;
    
    @Autowired
    private CacheService cacheService;

    @Autowired
    private HazelCastMessageListner hazelCastMessageListner;

    @PostMapping(path = "/search/{context}")
    public SearchListResponse getSearchedResult(@RequestBody SearchListRequest searchListRequest){
        log.info("Fetching Searched Data for Page --- {} with Page Size - {}" , searchListRequest.getCurrentPage() , searchListRequest.getPageSize());
        log.info("Fetching Cache for  Context - {} and Search Query  -- {}" , searchListRequest.getContext(),searchListRequest.getSearchQuery());
        log.info("Search Space --- {}",searchListRequest.getSearchSpace());
        SearchListResponse results =cacheService.findSearchedResults(searchListRequest);
        return results;
    }


    @GetMapping(path = "/{context}/{id}")
    public String getCache(@PathVariable("context") String context, @PathVariable("id") String cacheId){
        log.info("Fetching Cache for  Context - {} and id -- {}" , context,cacheId);
        IMap<String, HazelcastJsonValue> map = hazelcastInstance.getMap(context);
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(context);
        keyBuilder.append(DASH);
        keyBuilder.append(cacheId);
        String data = map.get(keyBuilder.toString()).getValue();
        return data;
    }


    @PutMapping(path = "/{context}/{id}")
    public String upsertCache(@PathVariable("context") String context, @PathVariable("id") String cacheId, @RequestBody String payload){
        log.info("Putting Cache for  Context - {} and id -- {}" , context,cacheId);
        log.info("Payload is - {}",payload);
        IMap<String, HazelcastJsonValue> map = hazelcastInstance.getMap(context);
        if(IS_LISTENR_ATTACHED_TO_CONTEXT.get(context)==null)
        {
            log.info("Initial Map Config - {}",IS_LISTENR_ATTACHED_TO_CONTEXT);
            log.info("Attached the Listener for Context --- {}",context);
            map.addEntryListener(hazelCastMessageListner,true);
            IS_LISTENR_ATTACHED_TO_CONTEXT.put(context,Boolean.TRUE);
            log.info("Current Map Config - {}",IS_LISTENR_ATTACHED_TO_CONTEXT);
        }
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(context);
        keyBuilder.append(DASH);
        keyBuilder.append(cacheId);
        map.put(keyBuilder.toString(),new HazelcastJsonValue(payload));
        String data = map.get(keyBuilder.toString()).getValue();
        return data;

    }

}
