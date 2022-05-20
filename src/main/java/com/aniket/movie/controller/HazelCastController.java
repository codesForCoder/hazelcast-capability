package com.aniket.movie.controller;


import com.aniket.movie.response.SearchListResponse;
import com.aniket.movie.service.CacheService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aniket.movie.constant.ApplicationConstant.DASH;
import static com.aniket.movie.constant.RestEndpoint.CACHE_ENDPOINT;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(CACHE_ENDPOINT)
public class HazelCastController {

    @Autowired
    private HazelcastInstance hazelcastInstance;
    
    @Autowired
    private CacheService cacheService;

    @GetMapping(path = "/{context}/{query}" ,params = {"page" , "limit"})
    public SearchListResponse getSearchedResult(@RequestParam int page , @RequestParam int limit ,@PathVariable("context") String context, @PathVariable("query") String query){
        log.info("Fetching Searched Data for Page --- {} with Page Size - {}" , page , limit);
        log.info("Fetching Cache for  Context - {} and Search Query  -- {}" , context,query);
        SearchListResponse results =cacheService.findSearchedResults(limit,page,context , query);
        return results;
    }
    
    @PostMapping(path = "/{context}")
    public ResponseEntity<String> createSearchTree(@PathVariable("context") String context){
        log.info("Creating  Search Tree for  Context - {} " , context);
        cacheService.createSrachTree(context);
        return new ResponseEntity<String>("Search Tree Creation Started ...",HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/{context}/{id}")
    public String getCache(@PathVariable("context") String context, @PathVariable("id") String cacheId){
        log.info("Fetching Cache for  Context - {} and id -- {}" , context,cacheId);
        IMap<String, String> map = hazelcastInstance.getMap(context);
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(context);
        keyBuilder.append(DASH);
        keyBuilder.append(cacheId);
        String data = map.get(keyBuilder.toString());
        return data;
    }
    @PutMapping(path = "/{context}/{id}")
    public String upsertCache(@PathVariable("context") String context, @PathVariable("id") String cacheId, @RequestBody String payload){
        log.info("Putting Cache for  Context - {} and id -- {}" , context,cacheId);
        log.info("Payload is - {}",payload);
        IMap<String, String> map = hazelcastInstance.getMap(context);
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(context);
        keyBuilder.append(DASH);
        keyBuilder.append(cacheId);
        map.put(keyBuilder.toString(),payload);
        String data = map.get(keyBuilder.toString());
        return data;

    }

}
