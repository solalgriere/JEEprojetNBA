package com.actorframework.core.supervision;

import com.actorframework.core.actor.Actor;
import com.actorframework.core.message.Message;

/**
 * Stratégie de supervision pour gérer les erreurs des acteurs.
 * Inspiré d'Akka SupervisorStrategy.
 */
public interface SupervisorStrategy {
    
    /**
     * Gère une erreur survenue dans un acteur
     * @param actor L'acteur qui a rencontré l'erreur
     * @param cause L'exception qui a causé l'erreur
     * @param message Le message qui était en cours de traitement
     * @return La décision de supervision (RESTART, RESUME, STOP, ESCALATE)
     */
    SupervisionDecision handleFailure(Actor actor, Throwable cause, Message message);
    
    enum SupervisionDecision {
        RESTART,  // Redémarrer l'acteur
        RESUME,   // Continuer l'exécution
        STOP,     // Arrêter l'acteur
        ESCALATE  // Escalader au superviseur parent
    }
}

