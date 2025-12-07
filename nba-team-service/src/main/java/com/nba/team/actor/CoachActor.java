package com.nba.team.actor;

import com.actorframework.core.actor.AbstractActor;
import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Acteur représentant un coach NBA.
 * Prend des décisions tactiques, effectue des changements et adapte la stratégie.
 */
@Slf4j
@Getter
@Setter
public class CoachActor extends AbstractActor {
    
    private String coachId;
    private String teamId;
    private List<String> activePlayers = new ArrayList<>();
    private String currentStrategy;
    private ActorRegistry actorRegistry;
    
    public CoachActor(String actorId) {
        super(actorId);
    }
    
    public CoachActor(String actorId, String coachId, String teamId, ActorRegistry actorRegistry) {
        super(actorId);
        this.coachId = coachId;
        this.teamId = teamId;
        this.actorRegistry = actorRegistry;
        this.currentStrategy = "DEFAULT";
    }
    
    @Override
    protected Object onReceive(Message message) {
        String messageType = message.getMessageType();
        
        switch (messageType) {
            case "SELECT_PLAYERS":
                return selectPlayers((List<String>) message.getPayload());
                
            case "MAKE_SUBSTITUTION":
                return makeSubstitution((Map<String, String>) message.getPayload());
                
            case "ADJUST_STRATEGY":
                return adjustStrategy((String) message.getPayload());
                
            case "GET_STRATEGY":
                return currentStrategy;
                
            case "GET_ACTIVE_PLAYERS":
                return activePlayers;
                
            case "PLAYER_INJURED":
                return handlePlayerInjury((String) message.getPayload());
                
            case "PLAYER_FATIGUED":
                return handlePlayerFatigue((String) message.getPayload());
                
            case "UPDATE_TEAM_RECORD":
                return updateTeamRecord((Map<String, Object>) message.getPayload());
                
            default:
                log.warn("Unknown message type: {}", messageType);
                return null;
        }
    }
    
    private String selectPlayers(List<String> playerIds) {
        if (playerIds == null) {
            log.warn("Received null playerIds list");
            return "Error: playerIds list is null";
        }
        
        activePlayers.clear();
        activePlayers.addAll(playerIds);
        log.info("Coach {} selected {} players for team {}", coachId, playerIds.size(), teamId);
        return "Players selected: " + playerIds.size();
    }
    
    private String makeSubstitution(Map<String, String> substitution) {
        String playerOut = substitution.get("playerOut");
        String playerIn = substitution.get("playerIn");
        
        if (activePlayers.remove(playerOut)) {
            activePlayers.add(playerIn);
            log.info("Coach {} substituted {} for {}", coachId, playerIn, playerOut);
            
            // Notifier les joueurs via le service Player
            notifyPlayerChange(playerOut, "OUT");
            notifyPlayerChange(playerIn, "IN");
            
            return "Substitution made";
        }
        return "Substitution failed: player not in game";
    }
    
    private String adjustStrategy(String newStrategy) {
        String oldStrategy = currentStrategy;
        currentStrategy = newStrategy;
        log.info("Coach {} changed strategy from {} to {}", coachId, oldStrategy, newStrategy);
        return "Strategy changed to: " + newStrategy;
    }
    
    private String handlePlayerInjury(String playerId) {
        log.warn("Coach {} handling injury for player {}", coachId, playerId);
        
        // Retirer le joueur blessé
        activePlayers.remove(playerId);
        
        // Trouver un remplaçant disponible
        // Dans un vrai système, on interrogerait le service Player
        return "Handled player injury: " + playerId;
    }
    
    private String handlePlayerFatigue(String playerId) {
        log.info("Coach {} handling fatigue for player {}", coachId, playerId);
        
        // Vérifier le niveau de fatigue via le service Player
        ActorRef playerRef = actorRegistry.resolveActor(
            "/nba-player-service/user/PlayerActor/player-" + playerId);
        
        if (playerRef != null) {
            Message checkMessage = new Message("GET_FATIGUE", null, true);
            Integer fatigue = (Integer) playerRef.ask(checkMessage, 3000);
            
            if (fatigue != null && fatigue > 80) {
                // Remplacer le joueur fatigué
                return "Player " + playerId + " is too fatigued, substitution recommended";
            }
        }
        
        return "Player fatigue checked";
    }
    
    private void notifyPlayerChange(String playerId, String action) {
        ActorRef playerRef = actorRegistry.resolveActor(
            "/nba-player-service/user/PlayerActor/player-" + playerId);
        
        if (playerRef != null) {
            Message message = new Message(
                action.equals("IN") ? "JOIN_GAME" : "LEAVE_GAME", 
                null, 
                false
            );
            playerRef.tell(message);
        }
    }
    
    private String updateTeamRecord(Map<String, Object> payload) {
        Boolean isWin = (Boolean) payload.get("isWin");
        if (isWin == null) {
            return "Invalid payload: isWin is required";
        }
        
        // Le CoachActor doit communiquer avec le TeamService pour mettre à jour le record
        // Pour l'instant, on va juste logger. Dans une vraie implémentation,
        // on utiliserait un service injecté ou une communication HTTP
        log.info("Team {} record update: {}", teamId, isWin ? "WIN" : "LOSS");
        
        // Envoyer un message au TeamController pour mettre à jour le record
        // Pour simplifier, on va utiliser un endpoint REST via WebClient
        // Mais pour l'instant, on va juste logger
        return "Team record update requested: " + (isWin ? "WIN" : "LOSS");
    }
}

