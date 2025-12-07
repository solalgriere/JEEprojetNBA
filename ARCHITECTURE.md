# Architecture du Framework d'Acteurs

## Vue d'Ensemble

Le framework est conçu pour être **générique et réutilisable**, séparé de l'application NBA qui l'utilise comme démonstration.

## Diagramme d'Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Eureka Server (8761)                      │
│              Service Discovery & Registration                 │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐
│ Player Service │  │  Team Service  │  │  Game Service  │
│   (Port 8081)  │  │  (Port 8082)   │  │  (Port 8083)   │
├────────────────┤  ├────────────────┤  ├────────────────┤
│ PlayerActor    │  │  CoachActor    │  │ ScoreboardActor│
│                │  │                 │  │                 │
│ REST API       │  │  REST API      │  │  REST API       │
└────────────────┘  └────────────────┘  └────────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                ┌───────────▼───────────┐
                │  Actor Framework Core │
                ├───────────────────────┤
                │  - ActorSystem        │
                │  - ActorRegistry      │
                │  - ActorLogger        │
                │  - Supervision        │
                └───────────────────────┘
```

## Composants Principaux

### 1. Framework Core (`actor-framework-core`)

#### ActorSystem
- **Responsabilité** : Gestion du cycle de vie des acteurs
- **Fonctionnalités** :
  - Création et destruction d'acteurs
  - Pool de threads pour l'exécution
  - Distribution des messages
  - Gestion de la scalabilité

#### Actor
- **Interface** : Définit le contrat pour tous les acteurs
- **Méthodes principales** :
  - `receive(Message)` : Traite un message
  - `preStart()` : Initialisation
  - `postStop()` : Nettoyage
  - `onError()` : Gestion d'erreurs

#### ActorRef
- **LocalActorRef** : Référence vers un acteur local
- **RemoteActorRef** : Référence vers un acteur distant (autre microservice)
- **Méthodes** :
  - `tell()` : Communication asynchrone
  - `ask()` : Communication synchrone avec timeout

#### Message
- **Structure** :
  - `messageId` : Identifiant unique
  - `senderPath` / `receiverPath` : Chemins des acteurs
  - `messageType` : Type de message
  - `payload` : Données du message
  - `requiresResponse` : Indique si une réponse est attendue

#### SupervisorStrategy
- **Stratégies** :
  - `RESTART` : Redémarrer l'acteur
  - `RESUME` : Continuer l'exécution
  - `STOP` : Arrêter l'acteur
  - `ESCALATE` : Escalader au parent

#### ActorLogger
- **Fonctionnalités** :
  - Logs structurés par acteur
  - Fichiers séparés (`logs/actors/{actorId}.log`)
  - Traçabilité complète des actions

#### ActorRegistry
- **Responsabilité** : Résolution des références d'acteurs
- **Fonctionnalités** :
  - Cache des acteurs locaux
  - Résolution des acteurs distants via Eureka
  - Découverte de services

### 2. Application NBA

#### Player Service
- **PlayerActor** :
  - État : joueur, statistiques, fatigue, blessure
  - Messages : `GET_PLAYER_INFO`, `UPDATE_STATS`, `PERFORM_ACTION`, etc.

#### Team Service
- **CoachActor** :
  - État : équipe, joueurs actifs, stratégie
  - Messages : `SELECT_PLAYERS`, `MAKE_SUBSTITUTION`, `ADJUST_STRATEGY`, etc.

#### Game Service
- **ScoreboardActor** :
  - État : match, score, quart-temps, chronomètre
  - Messages : `START_GAME`, `UPDATE_SCORE`, `PLAYER_ACTION`, etc.

## Flux de Communication

### Communication Intra-microservice

```
Client → REST Controller → ActorRegistry → LocalActorRef → Actor
```

### Communication Inter-microservice

```
Actor A (Service 1) → ActorRegistry → RemoteActorRef → 
  WebClient → HTTP → Eureka → Service 2 → ActorController → 
  ActorRegistry → LocalActorRef → Actor B
```

## Patterns Implémentés

### 1. Actor Model
- Encapsulation de l'état
- Communication par messages uniquement
- Pas de partage d'état mutable

### 2. Supervisor Pattern
- Hiérarchie de supervision
- Gestion automatique des erreurs
- Stratégies configurables

### 3. Service Discovery
- Eureka pour la découverte
- Résolution dynamique des services
- Health checks

### 4. Reactive Communication
- WebClient pour HTTP réactif
- Non-bloquant
- Gestion des timeouts

## Scalabilité

### Horizontale
- Plusieurs instances de chaque microservice
- Eureka gère la distribution de charge
- Acteurs distribués sur plusieurs instances

### Verticale
- Pool de threads configurable
- Queue de messages
- Gestion de la charge

## Sécurité

- Messages sérialisables
- Validation des chemins d'acteurs
- Gestion des timeouts
- Isolation des erreurs

## Monitoring

- Actuator endpoints (`/actuator/health`)
- Logs structurés par acteur
- Métriques du système d'acteurs
- Eureka dashboard

