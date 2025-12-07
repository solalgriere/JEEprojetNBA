package com.actorframework.core.actor;

import com.actorframework.core.message.Message;
import com.actorframework.core.supervision.SupervisorStrategy;
import com.actorframework.core.supervision.DefaultSupervisorStrategy;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe abstraite de base pour tous les acteurs.
 * Fournit une implémentation par défaut des fonctionnalités communes.
 */
@Getter
public abstract class AbstractActor implements Actor {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Setter
    protected String actorId;
    protected String actorPath;
    protected final AtomicBoolean active = new AtomicBoolean(false);
    protected SupervisorStrategy supervisorStrategy;
    
    public AbstractActor() {
        this.actorId = UUID.randomUUID().toString();
        this.supervisorStrategy = new DefaultSupervisorStrategy();
    }
    
    public AbstractActor(String actorId) {
        this.actorId = actorId;
        this.supervisorStrategy = new DefaultSupervisorStrategy();
    }
    
    @Override
    public String getActorPath() {
        if (actorPath == null) {
            actorPath = "/user/" + getClass().getSimpleName() + "/" + actorId;
        }
        return actorPath;
    }
    
    @Override
    public void preStart() {
        active.set(true);
        logger.info("Actor {} started at path {}", actorId, getActorPath());
    }
    
    @Override
    public void postStop() {
        active.set(false);
        logger.info("Actor {} stopped", actorId);
    }
    
    @Override
    public void onError(Throwable cause, Message message) {
        logger.error("Actor {} encountered error while processing message: {}", 
                     actorId, message, cause);
        
        if (supervisorStrategy != null) {
            supervisorStrategy.handleFailure(this, cause, message);
        }
    }
    
    @Override
    public boolean isActive() {
        return active.get();
    }
    
    @Override
    public void stop() {
        if (active.compareAndSet(true, false)) {
            postStop();
        }
    }
    
    @Override
    public SupervisorStrategy getSupervisorStrategy() {
        return supervisorStrategy;
    }
    
    /**
     * Méthode template pour le traitement des messages.
     * Les sous-classes doivent implémenter onReceive pour définir leur comportement.
     */
    @Override
    public Object receive(Message message) {
        if (!isActive()) {
            logger.warn("Actor {} is not active, ignoring message {}", actorId, message);
            return null;
        }
        
        try {
            logger.debug("Actor {} received message: {}", actorId, message);
            return onReceive(message);
        } catch (Exception e) {
            onError(e, message);
            throw e;
        }
    }
    
    /**
     * Méthode à implémenter par les sous-classes pour définir le comportement de l'acteur.
     */
    protected abstract Object onReceive(Message message);
}

