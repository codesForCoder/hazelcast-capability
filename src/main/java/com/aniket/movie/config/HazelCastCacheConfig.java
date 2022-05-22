package com.aniket.movie.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class HazelCastCacheConfig {

	@Value("${hazelcast.discovery.token}")
	private String hazelcastToken;
	
	@Value("${hazelcast.discovery.server}")
	private String hazelcastServer;

    public static final Map<String,Boolean> IS_LISTENR_ATTACHED_TO_CONTEXT = new ConcurrentHashMap<>();

    @Bean
    public HazelcastInstance hazelcastInstance(){
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().setRedoOperation(true);
        config.getNetworkConfig().getCloudConfig()
                .setDiscoveryToken(hazelcastToken)
                .setEnabled(true);
        config.setClusterName(hazelcastServer);
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        log.info("Connection is Successful !!");
         return client;
    }

//    private ClientConfig createClientConfig() {
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.addNearCacheConfig(createNearCacheConfig());
//        return clientConfig;
//    }
//
//    private NearCacheConfig createNearCacheConfig() {
//        NearCacheConfig nearCacheConfig = new NearCacheConfig();
//        nearCacheConfig.setName(CARS);
//        nearCacheConfig.setTimeToLiveSeconds(360);
//        nearCacheConfig.setMaxIdleSeconds(60);
//        return nearCacheConfig;
//    }
}
