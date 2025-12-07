package com.nba.game.controller;

import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.actor.ActorSystem;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import com.nba.game.actor.ScoreboardActor;
import com.nba.game.model.Game;
import com.nba.game.service.TeamValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    
    private final ActorSystem actorSystem;
    private final ActorRegistry actorRegistry;
    private final TeamValidationService teamValidationService;
    private final com.nba.game.service.TeamRecordService teamRecordService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGame(@RequestBody Game game) {
        // Valider que les deux équipes existent
        if (!teamValidationService.bothTeamsExist(game.getHomeTeamId(), game.getAwayTeamId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "One or both teams do not exist");
            
            boolean homeExists = teamValidationService.teamExists(game.getHomeTeamId());
            boolean awayExists = teamValidationService.teamExists(game.getAwayTeamId());
            
            StringBuilder message = new StringBuilder("Les équipes suivantes n'existent pas : ");
            if (!homeExists) {
                message.append(game.getHomeTeamId()).append(" (domicile)");
            }
            if (!awayExists) {
                if (!homeExists) message.append(", ");
                message.append(game.getAwayTeamId()).append(" (visiteur)");
            }
            
            errorResponse.put("message", message.toString());
            errorResponse.put("homeTeamExists", homeExists);
            errorResponse.put("awayTeamExists", awayExists);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Récupérer automatiquement tous les joueurs des équipes
        java.util.List<String> homePlayerIds = teamValidationService.getTeamPlayerIds(game.getHomeTeamId());
        java.util.List<String> awayPlayerIds = teamValidationService.getTeamPlayerIds(game.getAwayTeamId());
        
        if (homePlayerIds == null || homePlayerIds.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Home team not found");
            errorResponse.put("message", "Impossible de récupérer l'équipe domicile");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (awayPlayerIds == null || awayPlayerIds.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Away team not found");
            errorResponse.put("message", "Impossible de récupérer l'équipe visiteur");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (homePlayerIds.size() < 5) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Insufficient home players");
            errorResponse.put("message", "L'équipe domicile doit avoir au moins 5 joueurs (actuellement: " + homePlayerIds.size() + ")");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        if (awayPlayerIds.size() < 5) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Insufficient away players");
            errorResponse.put("message", "L'équipe visiteur doit avoir au moins 5 joueurs (actuellement: " + awayPlayerIds.size() + ")");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Utiliser tous les joueurs des équipes
        game.setHomePlayerIds(homePlayerIds);
        game.setAwayPlayerIds(awayPlayerIds);
        
        String actorId = "scoreboard-" + game.getId();
        ScoreboardActor actor = new ScoreboardActor(actorId, game, actorRegistry, teamRecordService);
        ActorRef actorRef = actorSystem.createActor(actor);
        actorRegistry.registerLocalActor(actor.getActorPath(), actorRef);
        
        Map<String, Object> response = new HashMap<>();
        response.put("actorId", actorId);
        response.put("path", actor.getActorPath());
        response.put("game", game);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{gameId}/start")
    public ResponseEntity<String> startGame(@PathVariable String gameId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-" + gameId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("START_GAME", null, true);
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response.toString());
    }
    
    @GetMapping("/{gameId}/score")
    public ResponseEntity<Object> getScore(@PathVariable String gameId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-" + gameId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("GET_SCORE", null, true);
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{gameId}/action")
    public ResponseEntity<String> recordAction(
            @PathVariable String gameId,
            @RequestBody Map<String, Object> action) {
        
        ActorRef actorRef = actorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-" + gameId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("PLAYER_ACTION", action, false);
        actorRef.tell(message);
        return ResponseEntity.accepted().body("Action recorded");
    }
    
    @PostMapping("/{gameId}/update-player-stats")
    public ResponseEntity<String> updatePlayerStats(
            @PathVariable String gameId,
            @RequestBody Map<String, Object> statsUpdate) {
        
        String playerId = (String) statsUpdate.get("playerId");
        if (playerId == null) {
            return ResponseEntity.badRequest().body("playerId is required");
        }
        
        // Envoyer la mise à jour directement au joueur
        ActorRef playerRef = actorRegistry.resolveActor(
            "/nba-player-service/user/PlayerActor/player-" + playerId);
        
        if (playerRef == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found: " + playerId);
        }
        
        Map<String, Object> updatePayload = new HashMap<>();
        if (statsUpdate.containsKey("points")) {
            updatePayload.put("points", statsUpdate.get("points"));
        }
        if (statsUpdate.containsKey("rebounds")) {
            updatePayload.put("rebounds", statsUpdate.get("rebounds"));
        }
        if (statsUpdate.containsKey("assists")) {
            updatePayload.put("assists", statsUpdate.get("assists"));
        }
        if (statsUpdate.containsKey("steals")) {
            updatePayload.put("steals", statsUpdate.get("steals"));
        }
        if (statsUpdate.containsKey("blocks")) {
            updatePayload.put("blocks", statsUpdate.get("blocks"));
        }
        
        Message message = new Message("UPDATE_STATS", updatePayload, false);
        playerRef.tell(message);
        
        return ResponseEntity.accepted().body("Player stats updated");
    }
    
    @PostMapping("/{gameId}/end")
    public ResponseEntity<String> endGame(@PathVariable String gameId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-" + gameId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("END_GAME", null, true);
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response.toString());
    }
    
    @PostMapping("/{gameId}/generate-complete")
    public ResponseEntity<Map<String, Object>> generateCompleteGame(@PathVariable String gameId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-" + gameId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("GENERATE_COMPLETE_GAME", null, true);
        Object response = actorRef.ask(message, 30000); // Timeout plus long pour générer toutes les stats
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", response != null ? response.toString() : "Game generated");
        result.put("gameId", gameId);
        
        return ResponseEntity.ok(result);
    }
}

