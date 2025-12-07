package com.nba.team.controller;

import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.actor.ActorSystem;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import com.nba.team.actor.CoachActor;
import com.nba.team.model.Team;
import com.nba.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    
    private final ActorSystem actorSystem;
    private final ActorRegistry actorRegistry;
    private final TeamService teamService;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTeam(@RequestBody Team team) {
        try {
            // Vérifier si l'équipe existe déjà
            if (teamService.teamExists(team.getId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Team already exists");
                response.put("message", "L'équipe avec l'ID " + team.getId() + " existe déjà");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Créer le coach actor - utiliser teamId pour la cohérence
            String coachActorId = "coach-" + team.getId();
            CoachActor coachActor = new CoachActor(coachActorId, coachActorId, team.getId(), actorRegistry);
            ActorRef coachRef = actorSystem.createActor(coachActor);
            String actorPath = coachActor.getActorPath();
            actorRegistry.registerLocalActor(actorPath, coachRef);
            
            // Ajouter l'équipe au service
            teamService.addTeam(team);
            
            log.info("Team created: {} with coach actor at path: {}", team.getId(), actorPath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("coachActorId", coachActorId);
            response.put("path", actorPath);
            response.put("team", team);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating team: {}", team.getId(), e);
            throw e;
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }
    
    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeam(@PathVariable String teamId) {
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }
    
    @GetMapping("/{teamId}/players")
    public ResponseEntity<Map<String, Object>> getTeamPlayers(@PathVariable String teamId) {
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("teamId", teamId);
        response.put("teamName", team.getName());
        response.put("playerIds", team.getPlayerIds());
        response.put("playerCount", team.getPlayerIds().size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{teamId}/exists")
    public ResponseEntity<Map<String, Boolean>> teamExists(@PathVariable String teamId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", teamService.teamExists(teamId));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{teamId}/add-player")
    public ResponseEntity<Map<String, Object>> addPlayerToTeam(
            @PathVariable String teamId,
            @RequestBody Map<String, String> request) {
        
        String playerId = request.get("playerId");
        if (playerId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "playerId is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        
        teamService.addPlayerToTeam(teamId, playerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("teamId", teamId);
        response.put("playerId", playerId);
        response.put("message", "Player added to team");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{teamId}/coach/select-players")
    public ResponseEntity<String> selectPlayers(
            @PathVariable String teamId,
            @RequestBody List<String> playerIds) {
        
        try {
            log.info("Attempting to select players for team: {}, players: {}", teamId, playerIds);
            
            // Essayer d'abord avec ActorRegistry
            String actorPath = "/user/CoachActor/coach-" + teamId;
            log.info("Looking for actor at path: {}", actorPath);
            
            ActorRef coachRef = actorRegistry.resolveActor(actorPath);
            
            // Si pas trouvé, essayer avec ActorSystem directement
            if (coachRef == null) {
                log.info("Actor not found in registry, trying ActorSystem directly");
                coachRef = actorSystem.getActorRef(actorPath);
            }
            
            // Si toujours pas trouvé, essayer de lister tous les acteurs pour debug
            if (coachRef == null) {
                log.error("Coach actor not found at path: {}. Available actors in system: {}", 
                         actorPath, actorSystem.getActiveActorCount());
                
                // Essayer avec le chemin exact tel qu'enregistré
                String exactPath = "/user/CoachActor/coach-" + teamId;
                coachRef = actorSystem.getActorRef(exactPath);
                
                if (coachRef == null) {
                    // Dernière tentative : essayer de recréer l'acteur si l'équipe existe
                    log.warn("Actor not found, but this might be a timing issue. Please try creating the team again or wait a moment.");
                    return ResponseEntity.status(404).body("Coach actor not found. Please make sure:\n1. You created the team with ID: " + teamId + "\n2. Wait a moment after creating the team\n3. Try again");
                }
            }
            
            log.info("Found coach actor successfully at path: {}", actorPath);
            
            log.debug("Found coach actor, sending SELECT_PLAYERS message");
            Message message = new Message("SELECT_PLAYERS", playerIds, true);
            Object response = coachRef.ask(message, 5000);
            
            if (response == null) {
                log.warn("No response from coach actor");
                return ResponseEntity.status(500).body("No response from coach actor");
            }
            
            log.info("Successfully selected players: {}", response);
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            log.error("Error selecting players for team {}", teamId, e);
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage() + " - " + e.getClass().getName());
        }
    }
    
    @PostMapping("/{teamId}/coach/substitute")
    public ResponseEntity<String> makeSubstitution(
            @PathVariable String teamId,
            @RequestBody Map<String, String> substitution) {
        
        ActorRef coachRef = actorRegistry.resolveActor("/user/CoachActor/coach-" + teamId);
        if (coachRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = new Message("MAKE_SUBSTITUTION", substitution, true);
        Object response = coachRef.ask(message, 5000);
        return ResponseEntity.ok(response.toString());
    }
    
    @PostMapping("/{teamId}/coach/strategy")
    public ResponseEntity<String> adjustStrategy(
            @PathVariable String teamId,
            @RequestBody Map<String, String> request) {
        
        ActorRef coachRef = actorRegistry.resolveActor("/user/CoachActor/coach-" + teamId);
        if (coachRef == null) {
            return ResponseEntity.notFound().build();
        }
        
        String strategy = request.get("strategy");
        Message message = new Message("ADJUST_STRATEGY", strategy, true);
        Object response = coachRef.ask(message, 5000);
        return ResponseEntity.ok(response.toString());
    }
    
    @PostMapping("/{teamId}/update-record")
    public ResponseEntity<Map<String, Object>> updateTeamRecord(
            @PathVariable String teamId,
            @RequestBody Map<String, Boolean> request) {
        
        Boolean isWin = request.get("isWin");
        if (isWin == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "isWin field is required");
            return ResponseEntity.badRequest().body(error);
        }
        
        Team team = teamService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (isWin) {
            team.setWins(team.getWins() + 1);
        } else {
            team.setLosses(team.getLosses() + 1);
        }
        
        teamService.updateTeam(team);
        
        Map<String, Object> response = new HashMap<>();
        response.put("teamId", teamId);
        response.put("wins", team.getWins());
        response.put("losses", team.getLosses());
        response.put("result", isWin ? "WIN" : "LOSS");
        
        log.info("Team {} record updated: {} wins, {} losses", 
                 teamId, team.getWins(), team.getLosses());
        
        return ResponseEntity.ok(response);
    }
}

