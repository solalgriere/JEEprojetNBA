package com.actorframework.core.actor;

import com.actorframework.core.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Implémentation d'ActorRef pour les acteurs locaux (même microservice).
 */
@Slf4j
@RequiredArgsConstructor
public class LocalActorRef implements ActorRef {
    
    private final Actor actor;
    private final ActorSystem actorSystem;
    
    @Override
    public String getPath() {
        return actor.getActorPath();
    }
    
    @Override
    public void tell(Message message) {
        if (!actor.isActive()) {
            log.warn("Actor {} is not active, message {} will be dropped", 
                     actor.getActorPath(), message.getMessageId());
            return;
        }
        
        message.setSenderPath(Thread.currentThread().getName());
        message.setReceiverPath(actor.getActorPath());
        
        actorSystem.sendMessage(actor, message);
    }
    
    @Override
    public Object ask(Message message, long timeoutMillis) {
        if (!actor.isActive()) {
            log.warn("Actor {} is not active, cannot process ask message", actor.getActorPath());
            return null;
        }
        
        message.setRequiresResponse(true);
        message.setSenderPath(Thread.currentThread().getName());
        message.setReceiverPath(actor.getActorPath());
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        actorSystem.sendMessageWithCallback(actor, message, future);
        
        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error waiting for response from actor {}", actor.getActorPath(), e);
            return null;
        }
    }
    
    @Override
    public boolean isAvailable() {
        return actor.isActive();
    }
}

