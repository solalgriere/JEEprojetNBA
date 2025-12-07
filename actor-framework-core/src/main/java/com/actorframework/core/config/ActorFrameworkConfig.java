package com.actorframework.core.config;

import com.actorframework.core.actor.ActorSystem;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.logging.ActorLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ActorFrameworkConfig {
    
    @Bean
    public ActorLogger actorLogger() {
        return new ActorLogger();
    }
    
    @Bean
    public ActorSystem actorSystem(ActorLogger actorLogger) {
        return new ActorSystem(actorLogger);
    }
    
    @Bean
    public ActorRegistry actorRegistry(
            org.springframework.cloud.client.discovery.DiscoveryClient discoveryClient,
            WebClient.Builder webClientBuilder) {
        return new ActorRegistry(discoveryClient, webClientBuilder);
    }
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

