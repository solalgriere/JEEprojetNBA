package com.actorframework.core.actor;

import com.actorframework.core.message.Message;

/**
 * Référence vers un acteur, permettant l'envoi de messages sans exposer l'acteur directement.
 * Inspiré d'Akka ActorRef.
 */
public interface ActorRef {
    
    /**
     * Obtient le chemin de l'acteur référencé
     */
    String getPath();
    
    /**
     * Envoie un message de manière asynchrone (fire-and-forget)
     */
    void tell(Message message);
    
    /**
     * Envoie un message de manière synchrone et attend une réponse
     */
    Object ask(Message message, long timeoutMillis);
    
    /**
     * Vérifie si l'acteur référencé est disponible
     */
    boolean isAvailable();
}

