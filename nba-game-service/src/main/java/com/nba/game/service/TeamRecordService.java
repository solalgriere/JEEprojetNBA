package com.nba.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour mettre à jour les records des équipes (victoires/défaites)
 * Utilise Eureka pour la découverte de services
 */
@Slf4j
@Service
public class TeamRecordService {
    
    private final WebClient webClient;
    private final DiscoveryClient discoveryClient;
    private static final String TEAM_SERVICE_NAME = "nba-team-service";
    
    public TeamRecordService(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.webClient = webClientBuilder.build();
        this.discoveryClient = discoveryClient;
    }
    
    /**
     * Obtient l'URL du service Team via Eureka
     */
    private String getTeamServiceUrl() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(TEAM_SERVICE_NAME);
            if (instances != null && !instances.isEmpty()) {
                ServiceInstance instance = instances.get(0);
                String url = "http://" + instance.getHost() + ":" + instance.getPort();
                log.debug("Found team service at: {}", url);
                return url;
            }
        } catch (Exception e) {
            log.warn("Error discovering team service via Eureka: {}", e.getMessage());
        }
        
        // Fallback pour développement local si Eureka n'est pas disponible
        log.warn("Team service not found in Eureka, using fallback URL");
        return "http://localhost:8082";
    }
    
    /**
     * Met à jour le record victoires/défaites d'une équipe
     */
    public void updateTeamRecord(String teamId, boolean isWin) {
        try {
            String serviceUrl = getTeamServiceUrl();
            Map<String, Boolean> requestBody = new HashMap<>();
            requestBody.put("isWin", isWin);
            
            Map<String, Object> response = webClient.post()
                .uri(serviceUrl + "/api/teams/{teamId}/update-record", teamId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (response != null) {
                log.info("✅ Team {} record updated: {} wins, {} losses", 
                        teamId, 
                        response.get("wins"), 
                        response.get("losses"));
            }
        } catch (Exception e) {
            log.error("❌ Error updating team record for {}: {}", teamId, e.getMessage(), e);
        }
    }
}

