package com.nba.player.service;

import com.nba.team.model.Team;
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
 * Service pour communiquer avec le Team Service
 * Utilise Eureka pour la découverte de services (conforme aux consignes)
 */
@Slf4j
@Service
public class TeamCommunicationService {
    
    private final WebClient webClient;
    private final DiscoveryClient discoveryClient;
    private static final String TEAM_SERVICE_NAME = "nba-team-service";
    
    public TeamCommunicationService(WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
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
     * Crée une équipe automatiquement
     */
    public Team createTeam(String teamId, String teamName) {
        try {
            Team team = new Team();
            team.setId(teamId);
            team.setName(teamName != null ? teamName : teamId);
            // wins and losses are already initialized to 0 in Team class
            
            String serviceUrl = getTeamServiceUrl();
            Map<String, Object> response = webClient.post()
                .uri(serviceUrl + "/api/teams/create")
                .bodyValue(team)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (response != null && response.containsKey("team")) {
                log.info("✅ Team {} created automatically", teamId);
                return team;
            }
        } catch (Exception e) {
            log.error("Error creating team automatically: {}", teamId, e);
        }
        return null;
    }
    
    /**
     * Ajoute un joueur à une équipe
     */
    public void addPlayerToTeam(String teamId, String playerId) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("playerId", playerId);
            
            String serviceUrl = getTeamServiceUrl();
            Map<String, Object> response = webClient.post()
                .uri(serviceUrl + "/api/teams/{teamId}/add-player", teamId)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(3))
                .block();
            
            if (response != null) {
                log.info("✅ Player {} added to team {}", playerId, teamId);
            }
        } catch (Exception e) {
            log.warn("Error adding player to team: {}", e.getMessage());
        }
    }
}
