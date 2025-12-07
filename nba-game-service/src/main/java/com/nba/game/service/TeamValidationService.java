package com.nba.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service pour valider l'existence des équipes
 * Utilise Eureka pour la découverte de services (conforme aux consignes)
 */
@Slf4j
@Service
public class TeamValidationService {
    
    private final WebClient webClient;
    private final DiscoveryClient discoveryClient;
    private static final String TEAM_SERVICE_NAME = "nba-team-service";
    
    public TeamValidationService(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
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
     * Vérifie si une équipe existe
     */
    public boolean teamExists(String teamId) {
        try {
            String serviceUrl = getTeamServiceUrl();
            Map<String, Boolean> response = webClient.get()
                .uri(serviceUrl + "/api/teams/{teamId}/exists", teamId)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(3))
                .block();
            
            return response != null && Boolean.TRUE.equals(response.get("exists"));
        } catch (Exception e) {
            log.warn("Error checking if team exists: {}", teamId, e);
            return false;
        }
    }
    
    /**
     * Vérifie si les deux équipes existent
     */
    public boolean bothTeamsExist(String homeTeamId, String awayTeamId) {
        return teamExists(homeTeamId) && teamExists(awayTeamId);
    }
    
    /**
     * Récupère les IDs des joueurs d'une équipe par son ID
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> getTeamPlayerIds(String teamId) {
        try {
            String serviceUrl = getTeamServiceUrl();
            Map<String, Object> teamData = webClient.get()
                .uri(serviceUrl + "/api/teams/{teamId}", teamId)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(3))
                .block();
            
            if (teamData != null && teamData.containsKey("playerIds")) {
                Object playerIdsObj = teamData.get("playerIds");
                if (playerIdsObj instanceof java.util.List) {
                    return (java.util.List<String>) playerIdsObj;
                }
            }
            
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            log.warn("Error getting team player IDs: {}", teamId, e);
            return new java.util.ArrayList<>();
        }
    }
}
