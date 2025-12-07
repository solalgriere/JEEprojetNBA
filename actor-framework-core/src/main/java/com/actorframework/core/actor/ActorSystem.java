package com.actorframework.core.actor;

import com.actorframework.core.message.Message;
import com.actorframework.core.logging.ActorLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Système d'acteurs gérant le cycle de vie et la communication entre acteurs.
 * Inspiré d'Akka ActorSystem.
 */
@Slf4j
public class ActorSystem {
    
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();
    private final Map<String, ActorRef> actorRefs = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final ActorLogger actorLogger;
    private final AtomicInteger actorCounter = new AtomicInteger(0);
    
    public ActorSystem(ActorLogger actorLogger) {
        this.actorLogger = actorLogger;
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        this.executorService = new ThreadPoolExecutor(
            corePoolSize,
            corePoolSize * 2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            r -> {
                Thread t = new Thread(r, "ActorSystem-" + actorCounter.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        );
    }
    
    /**
     * Crée un acteur et retourne sa référence
     */
    public <T extends Actor> ActorRef createActor(Class<T> actorClass, String actorId) {
        try {
            T actor = actorClass.getDeclaredConstructor(String.class).newInstance(actorId);
            return createActor(actor);
        } catch (Exception e) {
            log.error("Failed to create actor of type {} with id {}", actorClass.getName(), actorId, e);
            throw new RuntimeException("Failed to create actor", e);
        }
    }
    
    /**
     * Crée un acteur à partir d'une instance
     */
    public ActorRef createActor(Actor actor) {
        String path = actor.getActorPath();
        
        if (actors.containsKey(path)) {
            log.warn("Actor at path {} already exists", path);
            return actorRefs.get(path);
        }
        
        actors.put(path, actor);
        ActorRef ref = new LocalActorRef(actor, this);
        actorRefs.put(path, ref);
        
        actor.preStart();
        actorLogger.logActorCreation(actor);
        
        log.info("Created actor {} at path {}", actor.getActorId(), path);
        return ref;
    }
    
    /**
     * Obtient une référence vers un acteur par son chemin
     */
    public ActorRef getActorRef(String path) {
        return actorRefs.get(path);
    }
    
    /**
     * Obtient un acteur par son chemin
     */
    public Actor getActor(String path) {
        return actors.get(path);
    }
    
    /**
     * Envoie un message à un acteur de manière asynchrone
     */
    public void sendMessage(Actor actor, Message message) {
        executorService.submit(() -> {
            try {
                actorLogger.logMessageReceived(actor, message);
                actor.receive(message);
            } catch (Exception e) {
                log.error("Error processing message in actor {}", actor.getActorPath(), e);
                actor.onError(e, message);
            }
        });
    }
    
    /**
     * Envoie un message avec callback pour les communications synchrones
     */
    public void sendMessageWithCallback(Actor actor, Message message, CompletableFuture<Object> future) {
        executorService.submit(() -> {
            try {
                actorLogger.logMessageReceived(actor, message);
                Object response = actor.receive(message);
                future.complete(response);
                actorLogger.logMessageSent(actor, message, response);
            } catch (Exception e) {
                log.error("Error processing message in actor {}", actor.getActorPath(), e);
                actor.onError(e, message);
                future.completeExceptionally(e);
            }
        });
    }
    
    /**
     * Arrête un acteur
     */
    public void stopActor(String path) {
        Actor actor = actors.remove(path);
        if (actor != null) {
            actor.stop();
            actorRefs.remove(path);
            actorLogger.logActorStopped(actor);
            log.info("Stopped actor at path {}", path);
        }
    }
    
    /**
     * Arrête tous les acteurs et le système
     */
    public void shutdown() {
        log.info("Shutting down ActorSystem...");
        actors.values().forEach(Actor::stop);
        actors.clear();
        actorRefs.clear();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Obtient le nombre d'acteurs actifs
     */
    public int getActiveActorCount() {
        return (int) actors.values().stream()
            .filter(Actor::isActive)
            .count();
    }
}

