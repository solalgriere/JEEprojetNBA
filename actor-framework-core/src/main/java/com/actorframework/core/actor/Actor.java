package com.actorframework.core.actor;

import com.actorframework.core.message.Message;
import com.actorframework.core.supervision.SupervisorStrategy;

/**
 * Interface de base pour tous les acteurs du framework.
 * Inspiré d'Akka, chaque acteur est une boîte noire avec son propre état.
 */
public interface Actor {
    
    /**
     * Obtient l'identifiant unique de l'acteur
     */
    String getActorId();
    
    /**
     * Obtient le chemin complet de l'acteur (ex: /user/player/player1)
     */
    String getActorPath();
    
    /**
     * Traite un message reçu
     * @param message Le message à traiter
     * @return La réponse (peut être null pour les messages asynchrones)
     */
    Object receive(Message message);
    
    /**
     * Méthode appelée lors de la création de l'acteur
     */
    void preStart();
    
    /**
     * Méthode appelée lors de l'arrêt de l'acteur
     */
    void postStop();
    
    /**
     * Méthode appelée lorsqu'une erreur survient
     * @param cause L'exception qui a causé l'erreur
     * @param message Le message qui était en cours de traitement
     */
    void onError(Throwable cause, Message message);
    
    /**
     * Obtient la stratégie de supervision de l'acteur
     */
    SupervisorStrategy getSupervisorStrategy();
    
    /**
     * Vérifie si l'acteur est actif
     */
    boolean isActive();
    
    /**
     * Arrête l'acteur
     */
    void stop();
}

