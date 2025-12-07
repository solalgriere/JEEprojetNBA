package com.actorframework.core.communication;

import com.actorframework.core.actor.ActorRef;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registre des acteurs permettant de résoudre les références d'acteurs locaux et distants.
 * Utilise Eureka pour la découverte de services.
 */
@Slf4j
public class ActorRegistry {
    
    private final DiscoveryClient discoveryClient;
    private final WebClient.Builder webClientBuilder;
    private final Map<String, ActorRef> localActors = new ConcurrentHashMap<>();
    private final Map<String, ActorRef> remoteActors = new ConcurrentHashMap<>();
    
    public ActorRegistry(DiscoveryClient discoveryClient, WebClient.Builder webClientBuilder) {
        this.discoveryClient = discoveryClient;
        this.webClientBuilder = webClientBuilder;
    }
    
    /**
     * Enregistre un acteur local
     */
    public void registerLocalActor(String path, ActorRef actorRef) {
        localActors.put(path, actorRef);
        log.debug("Registered local actor: {}", path);
    }
    
    /**
     * Résout une référence d'acteur (local ou distant)
     */
    public ActorRef resolveActor(String path) {
        // Vérifier d'abord les acteurs locaux
        ActorRef localRef = localActors.get(path);
        if (localRef != null) {
            return localRef;
        }
        
        // Vérifier le cache des acteurs distants
        ActorRef cachedRemote = remoteActors.get(path);
        if (cachedRemote != null && cachedRemote.isAvailable()) {
            return cachedRemote;
        }
        
        // Essayer de résoudre un acteur distant
        // Format attendu: /service-name/actor-path
        if (path.startsWith("/")) {
            String[] parts = path.split("/", 3);
            if (parts.length >= 3) {
                String serviceName = parts[1];
                String actorPath = "/" + parts[2];
                
                // Vérifier si le service est disponible via Eureka
                if (isServiceAvailable(serviceName)) {
                    WebClient webClient = webClientBuilder.build();
                    ActorRef remoteRef = new RemoteActorRef(serviceName, actorPath, webClient, discoveryClient);
                    remoteActors.put(path, remoteRef);
                    return remoteRef;
                }
            }
        }
        
        log.warn("Could not resolve actor at path: {}", path);
        return null;
    }
    
    private boolean isServiceAvailable(String serviceName) {
        try {
            return !discoveryClient.getInstances(serviceName).isEmpty();
        } catch (Exception e) {
            log.error("Error checking service availability for {}", serviceName, e);
            return false;
        }
    }
    
    /**
     * Retire un acteur du registre
     */
    public void unregisterActor(String path) {
        localActors.remove(path);
        remoteActors.remove(path);
        log.debug("Unregistered actor: {}", path);
    }
}

