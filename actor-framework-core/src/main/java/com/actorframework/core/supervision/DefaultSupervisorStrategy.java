package com.actorframework.core.supervision;

import com.actorframework.core.actor.Actor;
import com.actorframework.core.message.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * Stratégie de supervision par défaut.
 * Redémarre l'acteur en cas d'erreur non fatale, l'arrête en cas d'erreur fatale.
 */
@Slf4j
public class DefaultSupervisorStrategy implements SupervisorStrategy {
    
    @Override
    public SupervisionDecision handleFailure(Actor actor, Throwable cause, Message message) {
        log.warn("Handling failure for actor {}: {}", actor.getActorPath(), cause.getMessage());
        
        // Erreurs fatales : arrêter l'acteur
        if (cause instanceof OutOfMemoryError || 
            cause instanceof StackOverflowError ||
            cause instanceof SecurityException) {
            log.error("Fatal error in actor {}, stopping", actor.getActorPath());
            return SupervisionDecision.STOP;
        }
        
        // Erreurs de validation : continuer
        if (cause instanceof IllegalArgumentException || 
            cause instanceof IllegalStateException) {
            log.warn("Validation error in actor {}, resuming", actor.getActorPath());
            return SupervisionDecision.RESUME;
        }
        
        // Autres erreurs : redémarrer l'acteur
        log.info("Restarting actor {} after error", actor.getActorPath());
        return SupervisionDecision.RESTART;
    }
}

