# 🔄 Communication Inter-Microservices dans le Projet

## Vue d'Ensemble

La communication entre microservices utilise **3 composants principaux** :
1. **Eureka** : Découverte de services
2. **WebClient** : Communication HTTP réactive
3. **ActorRegistry** : Résolution des références d'acteurs

---

## 📊 Architecture de Communication

```
┌─────────────────────────────────────────────────────────────┐
│                    Eureka Server (8761)                      │
│         Registre des services disponibles                    │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
│ Game Service  │  │ Player Service │  │  Team Service  │
│  (Port 8083)  │  │  (Port 8081)  │  │  (Port 8082)   │
│                │  │                │  │                │
│ ScoreboardActor│  │  PlayerActor   │  │  CoachActor    │
└───────┬────────┘  └───────┬────────┘  └───────┬────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                Communication HTTP via WebClient
```

---

## 🔍 Mécanisme Détaillé

### Étape 1 : Résolution de l'Acteur Distant

**Code :** `ActorRegistry.resolveActor()`

```java
// Exemple : ScoreboardActor veut communiquer avec PlayerActor
String playerPath = "/nba-player-service/user/PlayerActor/player-1";
ActorRef playerRef = actorRegistry.resolveActor(playerPath);
```

**Processus :**
1. Vérifie d'abord les acteurs locaux (cache)
2. Vérifie le cache des acteurs distants
3. Si pas trouvé, parse le chemin : `/service-name/actor-path`
4. Vérifie via Eureka si le service est disponible
5. Crée un `RemoteActorRef` si le service existe

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/communication/ActorRegistry.java` (lignes 39-72)

---

### Étape 2 : Découverte du Service via Eureka

**Code :** `ActorRegistry.isServiceAvailable()`

```java
private boolean isServiceAvailable(String serviceName) {
    try {
        return !discoveryClient.getInstances(serviceName).isEmpty();
    } catch (Exception e) {
        log.error("Error checking service availability for {}", serviceName, e);
        return false;
    }
}
```

**Processus :**
- Utilise `DiscoveryClient` (injecté par Spring Cloud Eureka)
- Vérifie si le service `nba-player-service` est enregistré dans Eureka
- Retourne `true` si au moins une instance est disponible

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/communication/ActorRegistry.java` (lignes 74-81)

---

### Étape 3 : Création de RemoteActorRef

**Code :** `ActorRegistry.resolveActor()` (suite)

```java
if (isServiceAvailable(serviceName)) {
    WebClient webClient = webClientBuilder.build();
    ActorRef remoteRef = new RemoteActorRef(serviceName, actorPath, webClient);
    remoteActors.put(path, remoteRef);
    return remoteRef;
}
```

**Processus :**
- Crée un `WebClient` pour les requêtes HTTP
- Crée un `RemoteActorRef` avec :
  - `serviceName` : "nba-player-service"
  - `actorPath` : "/user/PlayerActor/player-1"
  - `webClient` : Client HTTP réactif
- Met en cache la référence pour éviter de recréer

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/communication/RemoteActorRef.java`

---

### Étape 4 : Envoi du Message (Communication Asynchrone)

**Code :** `RemoteActorRef.tell()`

```java
@Override
public void tell(Message message) {
    message.setReceiverPath(getPath());
    
    webClient.post()
        .uri("http://" + serviceName + "/api/actors/message")
        .bodyValue(message)
        .retrieve()
        .bodyToMono(Void.class)
        .timeout(Duration.ofSeconds(5))
        .subscribe(
            result -> log.debug("Message sent successfully"),
            error -> log.error("Failed to send message", error)
        );
}
```

**Processus :**
1. Construit l'URL : `http://nba-player-service/api/actors/message`
2. Envoie une requête POST avec le message en JSON
3. Utilise `subscribe()` pour traitement asynchrone (non-bloquant)
4. Timeout de 5 secondes

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/communication/RemoteActorRef.java` (lignes 30-45)

---

### Étape 5 : Réception du Message (Communication Synchrone)

**Code :** `RemoteActorRef.ask()`

```java
@Override
public Object ask(Message message, long timeoutMillis) {
    message.setReceiverPath(getPath());
    message.setRequiresResponse(true);
    
    try {
        return webClient.post()
            .uri("http://" + serviceName + "/api/actors/message")
            .bodyValue(message)
            .retrieve()
            .bodyToMono(Object.class)
            .timeout(Duration.ofMillis(timeoutMillis))
            .block(); // Bloque jusqu'à la réponse
    } catch (Exception e) {
        log.error("Error in ask to remote actor", e);
        return null;
    }
}
```

**Processus :**
1. Même processus que `tell()` mais avec `requiresResponse = true`
2. Utilise `.block()` pour attendre la réponse (synchrone)
3. Retourne l'objet réponse ou `null` en cas d'erreur

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/communication/RemoteActorRef.java` (lignes 48-64)

---

### Étape 6 : Réception dans le Microservice Distant

**Code :** `ActorController.receiveMessage()`

```java
@PostMapping("/message")
public ResponseEntity<Object> receiveMessage(@RequestBody Message message) {
    log.info("Received message from remote actor: {}", message.getSenderPath());
    
    // Extraire le chemin local de l'acteur
    String receiverPath = message.getReceiverPath();
    if (receiverPath.contains("/user/")) {
        int index = receiverPath.indexOf("/user/");
        receiverPath = receiverPath.substring(index);
    }
    
    // Résoudre l'acteur local destinataire
    ActorRef actorRef = actorRegistry.resolveActor(receiverPath);
    
    if (message.isRequiresResponse()) {
        Object response = actorRef.ask(message, 5000);
        return ResponseEntity.ok(response);
    } else {
        actorRef.tell(message);
        return ResponseEntity.accepted().build();
    }
}
```

**Processus :**
1. Reçoit le message HTTP POST sur `/api/actors/message`
2. Extrait le chemin local de l'acteur (enlève le préfixe du service)
3. Résout l'acteur local via `ActorRegistry`
4. Envoie le message à l'acteur local
5. Retourne la réponse si `requiresResponse = true`

**Fichier :** `actor-framework-core/src/main/java/com/actorframework/core/controller/ActorController.java` (lignes 24-62)

---

## 🎯 Exemple Concret : ScoreboardActor → PlayerActor

### Scénario
Un panier est marqué pendant un match. Le `ScoreboardActor` (Game Service) doit notifier le `PlayerActor` (Player Service) pour mettre à jour ses statistiques.

### Code Source

**Fichier :** `nba-game-service/src/main/java/com/nba/game/actor/ScoreboardActor.java`

```java
private String handlePlayerAction(Map<String, Object> action) {
    String playerId = (String) action.get("playerId");
    String actionType = (String) action.get("action");
    
    // 1. Construire le chemin de l'acteur distant
    String playerPath = "/nba-player-service/user/PlayerActor/player-" + playerId;
    
    // 2. Résoudre l'acteur via ActorRegistry
    ActorRef playerRef = actorRegistry.resolveActor(playerPath);
    
    if (playerRef != null) {
        // 3. Créer le message
        Map<String, Object> actionPayload = new HashMap<>();
        actionPayload.put("action", actionType);
        actionPayload.put("points", 2);
        
        Message playerMessage = new Message("PERFORM_ACTION", actionPayload, false);
        
        // 4. Envoyer le message (asynchrone)
        playerRef.tell(playerMessage);
    }
}
```

### Flux Complet

```
1. ScoreboardActor.handlePlayerAction()
   ↓
2. actorRegistry.resolveActor("/nba-player-service/user/PlayerActor/player-1")
   ↓
3. ActorRegistry vérifie Eureka → Service "nba-player-service" disponible
   ↓
4. Crée RemoteActorRef(serviceName="nba-player-service", actorPath="/user/PlayerActor/player-1")
   ↓
5. RemoteActorRef.tell(message)
   ↓
6. WebClient POST → http://nba-player-service/api/actors/message
   ↓
7. Player Service reçoit sur ActorController.receiveMessage()
   ↓
8. ActorController résout l'acteur local : "/user/PlayerActor/player-1"
   ↓
9. Envoie le message à PlayerActor local
   ↓
10. PlayerActor.performAction() met à jour les stats
```

---

## 🔄 Exemple : Communication Synchrone

### Scénario
Le `ScoreboardActor` veut récupérer la liste des joueurs actifs d'une équipe via le `CoachActor`.

**Code :** `nba-game-service/src/main/java/com/nba/game/actor/ScoreboardActor.java`

```java
private void addPlayersToGame() {
    // Résoudre le coach de l'équipe à domicile
    ActorRef homeCoachRef = actorRegistry.resolveActor(
        "/nba-team-service/user/CoachActor/coach-" + game.getHomeTeamId());
    
    if (homeCoachRef != null) {
        // Communication SYNCHRONE (ask)
        Message getPlayersMessage = new Message("GET_ACTIVE_PLAYERS", null, true);
        Object homePlayersResponse = homeCoachRef.ask(getPlayersMessage, 3000);
        
        if (homePlayersResponse instanceof List) {
            List<String> homePlayerIds = (List<String>) homePlayersResponse;
            // Faire rejoindre les joueurs au match
            for (String playerId : homePlayerIds) {
                joinPlayerToGame(playerId);
            }
        }
    }
}
```

**Différence avec `tell()` :**
- `ask()` : Attend la réponse (bloque jusqu'à 3 secondes)
- `tell()` : Envoie et oublie (asynchrone)

---

## 🛠️ Technologies Utilisées

### 1. Spring Cloud Eureka

**Rôle :** Service Discovery
- Chaque microservice s'enregistre auprès d'Eureka
- Eureka maintient un registre des services disponibles
- `DiscoveryClient` permet de découvrir les services

**Configuration :**
```yaml
# application.yml de chaque service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 2. WebClient (Spring WebFlux)

**Rôle :** Communication HTTP réactive
- Non-bloquant (meilleure performance)
- Support des timeouts
- Gestion des erreurs avec Mono/Flux

**Avantages :**
- Plus performant que RestTemplate
- Asynchrone par défaut
- Compatible avec Spring WebFlux

### 3. ActorRegistry

**Rôle :** Résolution intelligente des acteurs
- Cache des acteurs locaux
- Cache des acteurs distants
- Découverte automatique via Eureka
- Création de `RemoteActorRef` à la volée

---

## 📝 Format des Chemins d'Acteurs

### Acteur Local
```
/user/PlayerActor/player-1
```

### Acteur Distant
```
/nba-player-service/user/PlayerActor/player-1
```

**Structure :**
- `/` : Début du chemin
- `nba-player-service` : Nom du service (découvert via Eureka)
- `/user/PlayerActor/player-1` : Chemin de l'acteur dans le service distant

---

## ✅ Avantages de cette Architecture

1. **Découverte Dynamique** : Pas besoin de configurer les URLs statiquement
2. **Scalabilité** : Plusieurs instances d'un service peuvent exister
3. **Résilience** : Eureka détecte les services indisponibles
4. **Performance** : WebClient est non-bloquant
5. **Transparence** : Même API (`tell`/`ask`) pour acteurs locaux et distants

---

## 🔍 Vérification

### Vérifier qu'Eureka fonctionne
1. Ouvrir http://localhost:8761
2. Vérifier que les 3 services sont enregistrés :
   - `NBA-PLAYER-SERVICE`
   - `NBA-TEAM-SERVICE`
   - `NBA-GAME-SERVICE`

### Vérifier la communication
1. Regarder les logs de `ScoreboardActor` :
   ```
   ✅ Sent PERFORM_ACTION message to player 1: action=SCORE, points=2
   ```

2. Regarder les logs de `PlayerActor` :
   ```
   🎮 Player player-1 received PERFORM_ACTION: {action=SCORE, points=2}
   ```

3. Vérifier les logs HTTP dans les services

---

## 📚 Résumé

La communication inter-microservices dans ce projet utilise :

1. **Eureka** → Découvre les services disponibles
2. **ActorRegistry** → Résout les acteurs distants
3. **RemoteActorRef** → Enveloppe la communication HTTP
4. **WebClient** → Envoie les requêtes HTTP réactives
5. **ActorController** → Reçoit les messages dans chaque service

**Résultat :** Les acteurs peuvent communiquer entre microservices de manière transparente, comme s'ils étaient dans le même service !


