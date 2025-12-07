package com.actorframework.core.controller;

import com.actorframework.core.actor.ActorRef;
import com.actorframework.core.actor.ActorSystem;
import com.actorframework.core.communication.ActorRegistry;
import com.actorframework.core.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {
    
    private final ActorSystem actorSystem;
    private final ActorRegistry actorRegistry;
    
    /**
     * Endpoint pour recevoir des messages d'acteurs distants
     */
    @PostMapping("/message")
    public ResponseEntity<Object> receiveMessage(@RequestBody Message message) {
        log.info("Received message from remote actor: {}", message.getSenderPath());
        
        // Extraire le chemin local de l'acteur (sans le préfixe du service)
        String receiverPath = message.getReceiverPath();
        if (receiverPath == null) {
            log.warn("Receiver path is null in message: {}", message.getMessageId());
            return ResponseEntity.badRequest().build();
        }
        
        // Si le chemin contient un préfixe de service, l'enlever
        if (receiverPath.contains("/user/")) {
            int index = receiverPath.indexOf("/user/");
            receiverPath = receiverPath.substring(index);
        }
        
        // Résoudre l'acteur local destinataire
        ActorRef actorRef = actorRegistry.resolveActor(receiverPath);
        if (actorRef == null) {
            // Essayer aussi avec le chemin complet depuis actorSystem
            actorRef = actorSystem.getActorRef(receiverPath);
        }
        
        if (actorRef == null) {
            log.warn("Could not resolve actor at path: {}", receiverPath);
            return ResponseEntity.notFound().build();
        }
        
        // Si une réponse est requise, utiliser ask
        if (message.isRequiresResponse()) {
            Object response = actorRef.ask(message, 5000);
            return ResponseEntity.ok(response);
        } else {
            // Sinon, utiliser tell (asynchrone)
            actorRef.tell(message);
            return ResponseEntity.accepted().build();
        }
    }
    
    /**
     * Endpoint pour obtenir des informations sur les acteurs
     */
    @GetMapping("/info")
    public ResponseEntity<ActorSystemInfo> getActorSystemInfo() {
        ActorSystemInfo info = new ActorSystemInfo();
        info.setActiveActorCount(actorSystem.getActiveActorCount());
        return ResponseEntity.ok(info);
    }
    
    @lombok.Data
    public static class ActorSystemInfo {
        private int activeActorCount;
    }
}

