package com.nba.team.service;

import com.nba.team.model.Team;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour gérer la liste des équipes créées
 */
@Service
@Getter
public class TeamService {
    
    // Map pour stocker les équipes créées (teamId -> Team)
    private final Map<String, Team> createdTeams = new ConcurrentHashMap<>();
    
    /**
     * Vérifie si une équipe existe
     */
    public boolean teamExists(String teamId) {
        return createdTeams.containsKey(teamId);
    }
    
    /**
     * Ajoute une équipe à la liste des équipes créées
     */
    public void addTeam(Team team) {
        createdTeams.put(team.getId(), team);
    }
    
    /**
     * Récupère une équipe par son ID
     */
    public Team getTeam(String teamId) {
        return createdTeams.get(teamId);
    }
    
    /**
     * Récupère toutes les équipes créées
     */
    public List<Team> getAllTeams() {
        return new ArrayList<>(createdTeams.values());
    }
    
    /**
     * Met à jour une équipe
     */
    public void updateTeam(Team team) {
        if (createdTeams.containsKey(team.getId())) {
            createdTeams.put(team.getId(), team);
        }
    }
    
    /**
     * Ajoute un joueur à une équipe
     */
    public void addPlayerToTeam(String teamId, String playerId) {
        Team team = createdTeams.get(teamId);
        if (team != null && !team.getPlayerIds().contains(playerId)) {
            team.getPlayerIds().add(playerId);
        }
    }
}
