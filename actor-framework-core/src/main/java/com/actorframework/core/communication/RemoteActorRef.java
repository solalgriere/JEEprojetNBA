package com.actorframework.core.communication;

import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Référence vers un acteur distant (dans un autre microservice).
 * Utilise WebClient pour la communication HTTP asynchrone.
 * Utilise Eureka pour la découverte de services (conforme aux consignes).
 */
@Slf4j
public class RemoteActorRef implements ActorRef {
    
    private final String serviceName;
    private final String actorPath;
    private final WebClient webClient;
    private final DiscoveryClient discoveryClient;
    
    public RemoteActorRef(String serviceName, String actorPath, WebClient webClient, DiscoveryClient discoveryClient) {
        this.serviceName = serviceName;
        this.actorPath = actorPath;
        this.webClient = webClient;
        this.discoveryClient = discoveryClient;
    }
    
    @Override
    public String getPath() {
        return "remote://" + serviceName + actorPath;
    }
    
    /**
     * Obtient l'URL du service via Eureka
     */
    private String getServiceUrl() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            if (instances != null && !instances.isEmpty()) {
                ServiceInstance instance = instances.get(0);
                String url = "http://" + instance.getHost() + ":" + instance.getPort();
                log.debug("Found service {} at: {}", serviceName, url);
                return url;
            }
        } catch (Exception e) {
            log.warn("Error discovering service {} via Eureka: {}", serviceName, e.getMessage());
        }
        
        // Fallback pour développement local si Eureka n'est pas disponible
        // Utiliser le port par défaut selon le nom du service
        String fallbackUrl = getFallbackUrl();
        log.warn("Service {} not found in Eureka, using fallback URL: {}", serviceName, fallbackUrl);
        return fallbackUrl;
    }
    
    /**
     * Obtient l'URL de fallback selon le nom du service
     */
    private String getFallbackUrl() {
        switch (serviceName) {
            case "nba-player-service":
                return "http://localhost:8081";
            case "nba-team-service":
                return "http://localhost:8082";
            case "nba-game-service":
                return "http://localhost:8083";
            default:
                return "http://localhost:8080";
        }
    }
    
    @Override
    public void tell(Message message) {
        message.setReceiverPath(getPath());
        
        String serviceUrl = getServiceUrl();
        webClient.post()
            .uri(serviceUrl + "/api/actors/message")
            .bodyValue(message)
            .retrieve()
            .bodyToMono(Void.class)
            .timeout(Duration.ofSeconds(5))
            .subscribe(
                result -> log.debug("Message {} sent successfully to remote actor {}", 
                    message.getMessageId(), getPath()),
                error -> log.error("Failed to send message {} to remote actor {}: {}", 
                    message.getMessageId(), getPath(), error.getMessage())
            );
    }
    
    @Override
    public Object ask(Message message, long timeoutMillis) {
        message.setReceiverPath(getPath());
        message.setRequiresResponse(true);
        
        try {
            String serviceUrl = getServiceUrl();
            return webClient.post()
                .uri(serviceUrl + "/api/actors/message")
                .bodyValue(message)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofMillis(timeoutMillis))
                .block();
        } catch (Exception e) {
            log.error("Error in ask to remote actor {}: {}", getPath(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean isAvailable() {
        try {
            String serviceUrl = getServiceUrl();
            webClient.get()
                .uri(serviceUrl + "/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

