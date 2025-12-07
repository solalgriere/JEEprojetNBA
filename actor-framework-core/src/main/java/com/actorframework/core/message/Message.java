package com.actorframework.core.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe représentant un message échangé entre acteurs.
 * Tous les messages doivent être sérialisables pour la communication inter-microservices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    
    private String messageId;
    private String senderPath;
    private String receiverPath;
    private String messageType;
    private Object payload;
    private LocalDateTime timestamp;
    private boolean requiresResponse;
    
    public Message(String messageType, Object payload) {
        this.messageId = UUID.randomUUID().toString();
        this.messageType = messageType;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.requiresResponse = false;
    }
    
    public Message(String messageType, Object payload, boolean requiresResponse) {
        this.messageId = UUID.randomUUID().toString();
        this.messageType = messageType;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.requiresResponse = requiresResponse;
    }
}

