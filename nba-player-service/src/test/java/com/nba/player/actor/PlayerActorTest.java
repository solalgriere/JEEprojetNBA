package com.nba.player.actor;

import com.actorframework.core.message.Message;
import com.nba.player.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerActorTest {
    
    private PlayerActor playerActor;
    
    @BeforeEach
    void setUp() {
        Player player = new Player("1", "LeBron James", "SF", 23, "LAL");
        playerActor = new PlayerActor("player-1", player);
        playerActor.preStart();
    }
    
    @Test
    void testGetPlayerInfo() {
        Message message = new Message("GET_PLAYER_INFO", null, true);
        Object response = playerActor.receive(message);
        
        assertNotNull(response);
        assertTrue(response instanceof Map);
    }
    
    @Test
    void testUpdateStats() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("points", 10);
        updates.put("rebounds", 5);
        updates.put("assists", 3);
        
        Message message = new Message("UPDATE_STATS", updates, true);
        Object response = playerActor.receive(message);
        
        assertEquals("Stats updated", response);
        assertEquals(10, playerActor.getStats().getPoints());
        assertEquals(5, playerActor.getStats().getRebounds());
        assertEquals(3, playerActor.getStats().getAssists());
    }
    
    @Test
    void testJoinGame() {
        Message message = new Message("JOIN_GAME", null, true);
        Object response = playerActor.receive(message);
        
        assertEquals("Joined game", response);
        assertTrue(playerActor.isInGame());
    }
    
    @Test
    void testPerformAction() {
        playerActor.setInGame(true);
        
        Message message = new Message("PERFORM_ACTION", "SCORE", true);
        Object response = playerActor.receive(message);
        
        assertTrue(response.toString().contains("SCORE"));
        assertTrue(playerActor.getStats().getPoints() > 0);
    }
}

