package com.nba.game.actor;

import com.actorframework.core.actor.AbstractActor;
import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import com.nba.game.model.Game;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Acteur représentant le tableau de score d'un match NBA.
 * Met à jour le score, gère le chronomètre et diffuse les mises à jour.
 */
@Slf4j
@Getter
@Setter
public class ScoreboardActor extends AbstractActor {
    
    private Game game;
    private ActorRegistry actorRegistry;
    private ScheduledExecutorService timer;
    private boolean gameRunning = false;
    private com.nba.game.service.TeamRecordService teamRecordService;
    
    public ScoreboardActor(String actorId) {
        super(actorId);
    }
    
    public ScoreboardActor(String actorId, Game game, ActorRegistry actorRegistry) {
        super(actorId);
        this.game = game;
        this.actorRegistry = actorRegistry;
        this.timer = new ScheduledThreadPoolExecutor(1);
    }
    
    public ScoreboardActor(String actorId, Game game, ActorRegistry actorRegistry, 
                          com.nba.game.service.TeamRecordService teamRecordService) {
        super(actorId);
        this.game = game;
        this.actorRegistry = actorRegistry;
        this.teamRecordService = teamRecordService;
        this.timer = new ScheduledThreadPoolExecutor(1);
    }
    
    @Override
    protected Object onReceive(Message message) {
        String messageType = message.getMessageType();
        
        switch (messageType) {
            case "START_GAME":
                return startGame();
                
            case "STOP_GAME":
                return stopGame();
                
            case "UPDATE_SCORE":
                return updateScore((Map<String, Object>) message.getPayload());
                
            case "GET_SCORE":
                return getScore();
                
            case "GET_GAME_STATUS":
                return getGameStatus();
                
            case "PLAYER_ACTION":
                return handlePlayerAction((Map<String, Object>) message.getPayload());
                
            case "END_QUARTER":
                return endQuarter();
                
            case "END_GAME":
                return endGame();
                
            case "GENERATE_COMPLETE_GAME":
                return generateCompleteGame();
                
            default:
                log.warn("Unknown message type: {}", messageType);
                return null;
        }
    }
    
    @Override
    public void postStop() {
        super.postStop();
        if (timer != null) {
            timer.shutdown();
        }
    }
    
    private String startGame() {
        if (gameRunning) {
            return "Game already running";
        }
        
        game.setStatus("IN_PROGRESS");
        gameRunning = true;
        
        // Récupérer les joueurs des équipes et les faire rejoindre le match
        addPlayersToGame();
        
        // Démarrer le chronomètre
        timer.scheduleAtFixedRate(() -> {
            if (gameRunning && game.getTimeRemaining() > 0) {
                game.setTimeRemaining(game.getTimeRemaining() - 1);
                
                // Fin du quart-temps
                if (game.getTimeRemaining() == 0) {
                    endQuarter();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
        
        log.info("Game {} started", game.getId());
        return "Game started";
    }
    
    /**
     * Utilise les joueurs sélectionnés lors de la création du match et les fait rejoindre le match
     */
    private void addPlayersToGame() {
        try {
            // Utiliser les joueurs sélectionnés pour l'équipe à domicile
            if (game.getHomePlayerIds() != null && !game.getHomePlayerIds().isEmpty()) {
                for (String playerId : game.getHomePlayerIds()) {
                    joinPlayerToGame(playerId);
                }
                log.info("Added {} home players to game {}", game.getHomePlayerIds().size(), game.getId());
            } else {
                log.warn("No home players selected for game {}", game.getId());
            }
            
            // Utiliser les joueurs sélectionnés pour l'équipe visiteuse
            if (game.getAwayPlayerIds() != null && !game.getAwayPlayerIds().isEmpty()) {
                for (String playerId : game.getAwayPlayerIds()) {
                    joinPlayerToGame(playerId);
                }
                log.info("Added {} away players to game {}", game.getAwayPlayerIds().size(), game.getId());
            } else {
                log.warn("No away players selected for game {}", game.getId());
            }
        } catch (Exception e) {
            log.error("Error adding players to game: {}", game.getId(), e);
        }
    }
    
    /**
     * Fait rejoindre un joueur au match en lui envoyant un message JOIN_GAME
     */
    private void joinPlayerToGame(String playerId) {
        try {
            ActorRef playerRef = actorRegistry.resolveActor(
                "/nba-player-service/user/PlayerActor/player-" + playerId);
            
            if (playerRef != null) {
                Message joinMessage = new Message("JOIN_GAME", null, false);
                playerRef.tell(joinMessage);
                log.debug("Sent JOIN_GAME message to player: {}", playerId);
            } else {
                log.warn("Player actor not found: player-{}", playerId);
            }
        } catch (Exception e) {
            log.error("Error joining player {} to game: {}", playerId, e.getMessage());
        }
    }
    
    private String stopGame() {
        gameRunning = false;
        game.setStatus("FINISHED");
        log.info("Game {} stopped. Final score: {} - {}", 
                 game.getId(), game.getHomeScore(), game.getAwayScore());
        return "Game stopped";
    }
    
    private Map<String, Object> getScore() {
        Map<String, Object> score = new HashMap<>();
        score.put("gameId", game.getId());
        score.put("homeScore", game.getHomeScore());
        score.put("awayScore", game.getAwayScore());
        score.put("quarter", game.getQuarter());
        score.put("timeRemaining", game.getTimeRemaining());
        score.put("status", game.getStatus());
        return score;
    }
    
    private Map<String, Object> getGameStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("game", game);
        status.put("running", gameRunning);
        return status;
    }
    
    private String updateScore(Map<String, Object> update) {
        String team = (String) update.get("team");
        int points = (Integer) update.get("points");
        
        if ("HOME".equals(team)) {
            game.setHomeScore(game.getHomeScore() + points);
        } else if ("AWAY".equals(team)) {
            game.setAwayScore(game.getAwayScore() + points);
        }
        
        log.info("Score updated: {} - {} (Game: {})", 
                 game.getHomeScore(), game.getAwayScore(), game.getId());
        return "Score updated";
    }
    
    private String handlePlayerAction(Map<String, Object> action) {
        String playerId = (String) action.get("playerId");
        String actionType = (String) action.get("action");
        String team = (String) action.get("team");
        
        if (playerId == null || playerId.isEmpty()) {
            log.warn("Player action received without playerId, action: {}", actionType);
        } else {
            // Notifier le joueur via le service Player
            String playerPath = "/nba-player-service/user/PlayerActor/player-" + playerId;
            ActorRef playerRef = actorRegistry.resolveActor(playerPath);
            
            if (playerRef != null) {
                // Créer un payload avec l'action et les points si c'est un panier
                Map<String, Object> actionPayload = new HashMap<>();
                actionPayload.put("action", actionType);
                if ("SCORE".equals(actionType) && action.containsKey("points")) {
                    actionPayload.put("points", action.get("points"));
                }
                
                try {
                    // Utiliser ask() au lieu de tell() pour s'assurer que le message est traité
                    // Mais avec un timeout court pour ne pas bloquer
                    Message playerMessage = new Message("PERFORM_ACTION", actionPayload, false);
                    playerRef.tell(playerMessage);
                    log.info("✅ Sent PERFORM_ACTION message to player {}: action={}, points={}", 
                             playerId, actionType, actionPayload.getOrDefault("points", "N/A"));
                } catch (Exception e) {
                    log.error("❌ Error sending message to player {}: {}", playerId, e.getMessage(), e);
                }
            } else {
                log.error("❌ Could not resolve player actor at path: {}. Player may not exist or service unavailable.", playerPath);
                log.error("   Make sure player {} exists and Player Service is running", playerId);
                // Continuer quand même pour mettre à jour le score
            }
        }
        
        // Mettre à jour le score si c'est un panier
        if ("SCORE".equals(actionType)) {
            int points = action.containsKey("points") ? (Integer) action.get("points") : 2;
            Map<String, Object> scoreUpdate = new HashMap<>();
            scoreUpdate.put("team", team);
            scoreUpdate.put("points", points);
            updateScore(scoreUpdate);
        }
        
        return "Action processed";
    }
    
    private String endQuarter() {
        game.setQuarter(game.getQuarter() + 1);
        
        if (game.getQuarter() > 4) {
            return endGame();
        } else {
            game.setTimeRemaining(720); // 12 minutes
            log.info("Quarter {} ended. Score: {} - {}", 
                     game.getQuarter() - 1, game.getHomeScore(), game.getAwayScore());
            return "Quarter ended, starting quarter " + game.getQuarter();
        }
    }
    
    /**
     * Termine le match et met à jour les victoires/défaites des équipes
     */
    private String endGame() {
        if (!gameRunning && "FINISHED".equals(game.getStatus())) {
            return "Game already finished";
        }
        
        gameRunning = false;
        game.setStatus("FINISHED");
        
        // Déterminer le gagnant et mettre à jour les victoires/défaites
        String winnerTeamId;
        String loserTeamId;
        
        if (game.getHomeScore() > game.getAwayScore()) {
            winnerTeamId = game.getHomeTeamId();
            loserTeamId = game.getAwayTeamId();
        } else if (game.getAwayScore() > game.getHomeScore()) {
            winnerTeamId = game.getAwayTeamId();
            loserTeamId = game.getHomeTeamId();
        } else {
            // Match nul (rare en NBA mais possible)
            log.info("Game {} ended in a tie: {} - {}", 
                     game.getId(), game.getHomeScore(), game.getAwayScore());
            return "Game finished in a tie: " + game.getHomeScore() + " - " + game.getAwayScore();
        }
        
        // Mettre à jour les victoires/défaites via le service Team
        updateTeamRecord(winnerTeamId, true);  // Victoire
        updateTeamRecord(loserTeamId, false); // Défaite
        
        log.info("Game {} finished. Final score: {} - {}. Winner: {}", 
                 game.getId(), game.getHomeScore(), game.getAwayScore(), winnerTeamId);
        
        return String.format("Game finished. Final score: %d - %d. Winner: %s", 
                            game.getHomeScore(), game.getAwayScore(), winnerTeamId);
    }
    
    /**
     * Met à jour le record victoires/défaites d'une équipe via le service
     */
    private void updateTeamRecord(String teamId, boolean isWin) {
        if (teamRecordService != null) {
            teamRecordService.updateTeamRecord(teamId, isWin);
        } else {
            log.warn("TeamRecordService not available, cannot update record for team: {}", teamId);
        }
    }
    
    /**
     * Génère un match complet avec score et statistiques pour tous les joueurs
     * Démontre le système d'acteurs en action avec communication inter-microservices
     */
    private String generateCompleteGame() {
        if (game.getStatus().equals("FINISHED")) {
            return "Game already finished";
        }
        
        log.info("🎮 Generating complete game for {}", game.getId());
        
        // Faire rejoindre tous les joueurs au match
        addPlayersToGame();
        
        // Générer un score réaliste (entre 90 et 110 points par équipe)
        java.util.Random random = new java.util.Random();
        int homeScore = 90 + random.nextInt(21); // 90-110
        int awayScore = 90 + random.nextInt(21); // 90-110
        
        // S'assurer qu'il y a un gagnant (pas d'égalité)
        while (homeScore == awayScore) {
            awayScore = 90 + random.nextInt(21);
        }
        
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);
        game.setQuarter(4);
        game.setTimeRemaining(0);
        
        log.info("📊 Generated score: {} - {} (Game: {})", homeScore, awayScore, game.getId());
        
        // Générer des statistiques pour chaque joueur avec contraintes précises
        generatePlayerStats(homeScore, awayScore);
        
        // Terminer le match
        String result = endGame();
        
        log.info("✅ Complete game generated and finished: {}", game.getId());
        return "Complete game generated. Final score: " + homeScore + " - " + awayScore + ". " + result;
    }
    
    /**
     * Génère des statistiques réalistes pour tous les joueurs et les distribue
     */
    private void generatePlayerStats(int homeScore, int awayScore) {
        java.util.Random random = new java.util.Random();
        
        // Générer stats pour l'équipe domicile
        if (game.getHomePlayerIds() != null && !game.getHomePlayerIds().isEmpty()) {
            generateTeamStats(game.getHomePlayerIds(), homeScore, "HOME", random);
        }
        
        // Générer stats pour l'équipe visiteur
        if (game.getAwayPlayerIds() != null && !game.getAwayPlayerIds().isEmpty()) {
            generateTeamStats(game.getAwayPlayerIds(), awayScore, "AWAY", random);
        }
    }
    
    /**
     * Génère et distribue les statistiques pour une équipe avec contraintes précises
     * - Points : somme = teamScore
     * - Rebonds : somme = 30
     * - Passes décisives : somme = 25
     */
    private void generateTeamStats(java.util.List<String> playerIds, int teamScore, String team, java.util.Random random) {
        int numPlayers = playerIds.size();
        if (numPlayers == 0) return;
        
        // Répartir les points : somme = teamScore
        int totalPoints = teamScore;
        int[] playerPoints = new int[numPlayers];
        
        // Distribuer les points (les meilleurs joueurs marquent plus)
        for (int i = 0; i < numPlayers && totalPoints > 0; i++) {
            int points;
            if (i < 2) {
                // Top 2 joueurs : 15-30 points
                points = Math.min(totalPoints, 15 + random.nextInt(16));
            } else if (i < 4) {
                // Joueurs 3-4 : 8-18 points
                points = Math.min(totalPoints, 8 + random.nextInt(11));
            } else {
                // Autres joueurs : 2-12 points
                points = Math.min(totalPoints, 2 + random.nextInt(11));
            }
            
            playerPoints[i] = Math.min(points, totalPoints);
            totalPoints -= playerPoints[i];
        }
        
        // Distribuer le reste équitablement
        if (totalPoints > 0) {
            for (int i = 0; i < numPlayers && totalPoints > 0; i++) {
                int add = Math.min(1, totalPoints);
                playerPoints[i] += add;
                totalPoints -= add;
            }
        }
        
        // Répartir les rebonds : somme = 30
        int totalRebounds = 30;
        int[] playerRebounds = new int[numPlayers];
        
        for (int i = 0; i < numPlayers && totalRebounds > 0; i++) {
            int rebounds;
            if (i < 2) {
                // Top 2 : 5-8 rebonds
                rebounds = Math.min(totalRebounds, 5 + random.nextInt(4));
            } else {
                // Autres : 2-6 rebonds
                rebounds = Math.min(totalRebounds, 2 + random.nextInt(5));
            }
            
            playerRebounds[i] = Math.min(rebounds, totalRebounds);
            totalRebounds -= playerRebounds[i];
        }
        
        // Distribuer le reste
        if (totalRebounds > 0) {
            for (int i = 0; i < numPlayers && totalRebounds > 0; i++) {
                int add = Math.min(1, totalRebounds);
                playerRebounds[i] += add;
                totalRebounds -= add;
            }
        }
        
        // Répartir les passes décisives : somme = 25
        int totalAssists = 25;
        int[] playerAssists = new int[numPlayers];
        
        for (int i = 0; i < numPlayers && totalAssists > 0; i++) {
            int assists;
            if (i == 0) {
                // Meilleur passeur : 6-10 passes
                assists = Math.min(totalAssists, 6 + random.nextInt(5));
            } else if (i < 3) {
                // Joueurs 2-3 : 3-6 passes
                assists = Math.min(totalAssists, 3 + random.nextInt(4));
            } else {
                // Autres : 1-4 passes
                assists = Math.min(totalAssists, 1 + random.nextInt(4));
            }
            
            playerAssists[i] = Math.min(assists, totalAssists);
            totalAssists -= playerAssists[i];
        }
        
        // Distribuer le reste
        if (totalAssists > 0) {
            for (int i = 0; i < numPlayers && totalAssists > 0; i++) {
                int add = Math.min(1, totalAssists);
                playerAssists[i] += add;
                totalAssists -= add;
            }
        }
        
        // Vérification et correction des totaux
        int sumPoints = 0, sumRebounds = 0, sumAssists = 0;
        for (int i = 0; i < numPlayers; i++) {
            sumPoints += playerPoints[i];
            sumRebounds += playerRebounds[i];
            sumAssists += playerAssists[i];
        }
        
        // Ajuster si nécessaire
        if (sumPoints != teamScore) {
            int diff = teamScore - sumPoints;
            playerPoints[0] += diff; // Ajuster sur le premier joueur
        }
        if (sumRebounds != 30) {
            int diff = 30 - sumRebounds;
            playerRebounds[0] += diff;
        }
        if (sumAssists != 25) {
            int diff = 25 - sumAssists;
            playerAssists[0] += diff;
        }
        
        // Générer et envoyer les stats pour chaque joueur
        for (int i = 0; i < numPlayers; i++) {
            String playerId = playerIds.get(i);
            int points = playerPoints[i];
            int rebounds = playerRebounds[i];
            int assists = playerAssists[i];
            
            // Envoyer les stats au joueur via son acteur (sans steals, blocks, fatigue)
            updatePlayerStats(playerId, points, rebounds, assists, 0, 0);
        }
        
        log.info("📊 Generated stats for {} players in team {}: {}pts total, {}reb total, {}ast total", 
                 numPlayers, team, teamScore, 30, 25);
    }
    
    /**
     * Met à jour les statistiques d'un joueur via son acteur
     * (points, rebounds, assists uniquement - pas de steals, blocks, fatigue)
     */
    private void updatePlayerStats(String playerId, int points, int rebounds, int assists, int steals, int blocks) {
        try {
            String playerPath = "/nba-player-service/user/PlayerActor/player-" + playerId;
            ActorRef playerRef = actorRegistry.resolveActor(playerPath);
            
            if (playerRef != null) {
                Map<String, Object> statsUpdate = new HashMap<>();
                if (points > 0) statsUpdate.put("points", points);
                if (rebounds > 0) statsUpdate.put("rebounds", rebounds);
                if (assists > 0) statsUpdate.put("assists", assists);
                // Ne pas inclure steals, blocks, fatigue
                
                Message updateMessage = new Message("UPDATE_STATS", statsUpdate, false);
                playerRef.tell(updateMessage);
                
                log.debug("📈 Updated stats for player {}: {}pts, {}reb, {}ast", 
                         playerId, points, rebounds, assists);
            } else {
                log.warn("⚠️ Could not find player actor: {}", playerId);
            }
        } catch (Exception e) {
            log.error("❌ Error updating stats for player {}: {}", playerId, e.getMessage(), e);
        }
    }
}

