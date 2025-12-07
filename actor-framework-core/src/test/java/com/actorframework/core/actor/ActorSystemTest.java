package com.actorframework.core.actor;

import com.actorframework.core.logging.ActorLogger;
import com.actorframework.core.message.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorSystemTest {
    
    private ActorSystem actorSystem;
    private ActorLogger actorLogger;
    
    @BeforeEach
    void setUp() {
        actorLogger = new ActorLogger();
        actorSystem = new ActorSystem(actorLogger);
    }
    
    @AfterEach
    void tearDown() {
        actorSystem.shutdown();
        actorLogger.close();
    }
    
    @Test
    void testCreateActor() {
        TestActor actor = new TestActor("test-1");
        ActorRef ref = actorSystem.createActor(actor);
        
        assertNotNull(ref);
        assertEquals("test-1", actor.getActorId());
        assertTrue(actor.isActive());
    }
    
    @Test
    void testActorReceiveMessage() {
        TestActor actor = new TestActor("test-1");
        ActorRef ref = actorSystem.createActor(actor);
        
        Message message = new Message("TEST", "Hello", true);
        Object response = ref.ask(message, 5000);
        
        assertEquals("Received: Hello", response);
    }
    
    @Test
    void testStopActor() {
        TestActor actor = new TestActor("test-1");
        ActorRef ref = actorSystem.createActor(actor);
        
        assertTrue(actor.isActive());
        actorSystem.stopActor(actor.getActorPath());
        assertFalse(actor.isActive());
    }
    
    // Classe de test pour les acteurs
    static class TestActor extends AbstractActor {
        private String lastMessage;
        
        public TestActor(String actorId) {
            super(actorId);
        }
        
        @Override
        protected Object onReceive(Message message) {
            lastMessage = (String) message.getPayload();
            return "Received: " + lastMessage;
        }
    }
}

