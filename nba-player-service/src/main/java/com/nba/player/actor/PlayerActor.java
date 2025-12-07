package com.nba.player.actor;

import com.actorframework.core.actor.AbstractActor;
import com.actorframework.core.message.Message;
import com.nba.player.model.Player;
import com.nba.player.model.PlayerStats;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Acteur représentant un joueur NBA.
 * Gère l'état du joueur, ses statistiques et sa forme physique.
 */
@Slf4j
@Getter
@Setter
public class PlayerActor extends AbstractActor {
    
    private Player player;
    private PlayerStats stats;
    private int fatigueLevel; // 0-100
    private boolean injured;
    private boolean inGame;
    
    public PlayerActor(String actorId) {
        super(actorId);
        this.stats = new PlayerStats();
        this.fatigueLevel = 0;
        this.injured = false;
        this.inGame = false;
    }
    
    public PlayerActor(String actorId, Player player) {
        super(actorId);
        this.player = player;
        this.stats = new PlayerStats();
        this.fatigueLevel = 0;
        this.injured = false;
        this.inGame = false;
    }
    
    @Override
    protected Object onReceive(Message message) {
        String messageType = message.getMessageType();
        
        switch (messageType) {
            case "GET_PLAYER_INFO":
                return getPlayerInfo();
                
            case "UPDATE_STATS":
                return updateStats((Map<String, Object>) message.getPayload());
                
            case "GET_STATS":
                return stats;
                
            case "SET_FATIGUE":
                setFatigueLevel((Integer) message.getPayload());
                return "Fatigue updated";
                
            case "GET_FATIGUE":
                return fatigueLevel;
                
            case "SET_INJURED":
                setInjured((Boolean) message.getPayload());
                return "Injury status updated";
                
            case "IS_AVAILABLE":
                return isAvailable();
                
            case "JOIN_GAME":
                return joinGame();
                
            case "LEAVE_GAME":
                return leaveGame();
                
            case "PERFORM_ACTION":
                // Le payload peut être une String (ancien format) ou une Map (nouveau format)
                Object payload = message.getPayload();
                if (payload instanceof Map) {
                    return performAction((Map<String, Object>) payload);
                } else if (payload instanceof String) {
                    return performAction((String) payload);
                } else {
                    log.warn("Unknown payload type for PERFORM_ACTION: {}", payload);
                    return "Invalid action payload";
                }
                
            default:
                log.warn("Unknown message type: {}", messageType);
                return null;
        }
    }
    
    private Map<String, Object> getPlayerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("actorId", getActorId());
        info.put("player", player);
        info.put("stats", stats);
        info.put("fatigueLevel", fatigueLevel);
        info.put("injured", injured);
        info.put("inGame", inGame);
        return info;
    }
    
    private String updateStats(Map<String, Object> updates) {
        if (updates.containsKey("points")) {
            stats.addPoints((Integer) updates.get("points"));
        }
        if (updates.containsKey("rebounds")) {
            stats.addRebounds((Integer) updates.get("rebounds"));
        }
        if (updates.containsKey("assists")) {
            stats.addAssists((Integer) updates.get("assists"));
        }
        if (updates.containsKey("steals")) {
            stats.addSteals((Integer) updates.get("steals"));
        }
        if (updates.containsKey("blocks")) {
            stats.addBlocks((Integer) updates.get("blocks"));
        }
        
        // Augmenter la fatigue avec chaque action
        fatigueLevel = Math.min(100, fatigueLevel + 1);
        
        log.info("Player {} stats updated: {}", getActorId(), stats);
        return "Stats updated";
    }
    
    private boolean isAvailable() {
        return !injured && fatigueLevel < 90;
    }
    
    private String joinGame() {
        if (!isAvailable()) {
            return "Player not available";
        }
        inGame = true;
        log.info("Player {} joined the game", getActorId());
        return "Joined game";
    }
    
    private String leaveGame() {
        inGame = false;
        log.info("Player {} left the game", getActorId());
        return "Left game";
    }
    
    private String performAction(String action) {
        Map<String, Object> actionData = new HashMap<>();
        actionData.put("action", action);
        return performAction(actionData);
    }
    
    private String performAction(Map<String, Object> actionData) {
        log.info("🎮 Player {} received PERFORM_ACTION: {}", getActorId(), actionData);
        
        String action = (String) actionData.get("action");
        if (action == null) {
            log.warn("PERFORM_ACTION received without action type");
            return "Invalid action";
        }
        
        // Si le joueur n'est pas encore dans le match, le faire rejoindre automatiquement
        if (!inGame) {
            log.info("Player {} not in game, auto-joining for action {}", getActorId(), action);
            inGame = true;
        }
        
        // Sauvegarder les stats avant la mise à jour pour le log
        int pointsBefore = stats.getPoints();
        int reboundsBefore = stats.getRebounds();
        int assistsBefore = stats.getAssists();
        
        // Simuler une action de jeu
        Map<String, Object> statUpdate = new HashMap<>();
        switch (action) {
            case "SCORE":
                // Utiliser les points passés dans l'action, ou 2 par défaut
                int points = actionData.containsKey("points") ? 
                    ((Number) actionData.get("points")).intValue() : 2;
                statUpdate.put("points", points);
                log.info("🏀 Player {} scored {} points (was: {}, now: {})", 
                        getActorId(), points, pointsBefore, pointsBefore + points);
                break;
            case "REBOUND":
                statUpdate.put("rebounds", 1);
                log.info("🏀 Player {} got a rebound (was: {}, now: {})", 
                        getActorId(), reboundsBefore, reboundsBefore + 1);
                break;
            case "ASSIST":
                statUpdate.put("assists", 1);
                log.info("🏀 Player {} made an assist (was: {}, now: {})", 
                        getActorId(), assistsBefore, assistsBefore + 1);
                break;
            case "STEAL":
                statUpdate.put("steals", 1);
                log.info("🏀 Player {} made a steal", getActorId());
                break;
            case "BLOCK":
                statUpdate.put("blocks", 1);
                log.info("🏀 Player {} made a block", getActorId());
                break;
            default:
                log.warn("Unknown action type: {}", action);
                return "Unknown action: " + action;
        }
        
        updateStats(statUpdate);
        log.info("✅ Player {} stats updated after {}: Points={}, Rebounds={}, Assists={}, Steals={}, Blocks={}", 
                 getActorId(), action, stats.getPoints(), stats.getRebounds(), 
                 stats.getAssists(), stats.getSteals(), stats.getBlocks());
        return "Action performed: " + action;
    }
}

