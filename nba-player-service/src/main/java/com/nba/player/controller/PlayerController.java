package com.nba.player.controller;

import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.actor.ActorSystem;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import com.nba.player.actor.PlayerActor;
import com.nba.player.model.Player;
import com.nba.player.service.TeamCommunicationService;
import com.nba.team.model.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    
    private final ActorSystem actorSystem;
    private final ActorRegistry actorRegistry;
    private final TeamCommunicationService teamCommunicationService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestBody Player player) {
        // Valider que l'équipe est fournie et existe avant de créer le joueur
        if (player.getTeamId() == null || player.getTeamId().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Team is required");
            errorResponse.put("message", "L'équipe est obligatoire. Veuillez créer une équipe d'abord, puis sélectionnez-la lors de la création du joueur.");
            log.warn("Cannot create player {}: teamId is required", player.getId());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Vérifier que l'équipe existe
        if (!teamCommunicationService.teamExists(player.getTeamId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Team does not exist");
            errorResponse.put("message", "L'équipe avec l'ID " + player.getTeamId() + " n'existe pas. Veuillez créer l'équipe d'abord.");
            errorResponse.put("teamId", player.getTeamId());
            log.warn("Cannot create player {}: team {} does not exist", player.getId(), player.getTeamId());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Ajouter le joueur à l'équipe
        teamCommunicationService.addPlayerToTeam(player.getTeamId(), player.getId());
        log.info("✅ Player {} added to team {}", player.getId(), player.getTeamId());
        
        String actorId = "player-" + player.getId();
        PlayerActor actor = new PlayerActor(actorId, player);
        ActorRef actorRef = actorSystem.createActor(actor);
        actorRegistry.registerLocalActor(actor.getActorPath(), actorRef);
        
        Map<String, Object> response = new HashMap<>();
        response.put("actorId", actorId);
        response.put("path", actor.getActorPath());
        response.put("player", player);
        if (player.getTeamId() != null) {
            response.put("teamId", player.getTeamId());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{actorId}/info")
    public ResponseEntity<Object> getPlayerInfo(@PathVariable String actorId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/PlayerActor/" + actorId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("GET_PLAYER_INFO", null, true);
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{actorId}/action")
    public ResponseEntity<String> performAction(
            @PathVariable String actorId,
            @RequestBody Map<String, String> request) {
        
        ActorRef actorRef = actorRegistry.resolveActor("/user/PlayerActor/" + actorId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        String action = request.get("action");
        Message message = new Message("PERFORM_ACTION", action, false);
        actorRef.tell(message);
        
        return ResponseEntity.accepted().body("Action sent");
    }
    
    @PostMapping("/{actorId}/join-game")
    public ResponseEntity<String> joinGame(@PathVariable String actorId) {
        ActorRef actorRef = actorRegistry.resolveActor("/user/PlayerActor/" + actorId);
        if (actorRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("JOIN_GAME", null, true);
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response.toString());
    }
}

