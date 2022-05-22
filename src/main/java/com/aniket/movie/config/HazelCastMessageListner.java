package com.aniket.movie.config;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.MapEvent;
import com.hazelcast.map.listener.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HazelCastMessageListner implements EntryAddedListener<String, String>,
        EntryRemovedListener<String, String>,
        EntryUpdatedListener<String, String>,
        EntryEvictedListener<String, String>,
        EntryLoadedListener<String,String>,
        MapEvictedListener,
        MapClearedListener  {

    // Autowiring Kafka Template
    @Autowired
    private KafkaProducer kafkaProducer;




    private static final String TOPIC = "Hazelcast-Events";

    @Override
    public void entryAdded( EntryEvent<String, String> event ) {
        log.info( "Entry Added: {}" , event );
    }

    @Override
    public void entryRemoved( EntryEvent<String, String> event ) {
        log.info( "Entry Removed: {}" , event );
    }

    @Override
    public void entryUpdated( EntryEvent<String, String> event ) {
        log.info( "Entry Updated: {}" , event );
        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setCacheKeyName(event.getKey());
        kafkaMessage.setContext(event.getName());
        kafkaProducer.produce( kafkaMessage);
    }

    @Override
    public void entryEvicted( EntryEvent<String, String> event ) {
        log.info( "Entry Evicted: {}" , event );
    }

    @Override
    public void entryLoaded( EntryEvent<String, String> event ) {
        log.info( "Entry Loaded: {}" , event );
    }

    @Override
    public void mapEvicted( MapEvent event ) {
        log.info( "Map Evicted: {}" , event );
    }

    @Override
    public void mapCleared( MapEvent event ) {
        log.info( "Map Cleared: {}" , event );
    }
}
