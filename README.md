# Framework d'Acteurs Distribué - Application NBA

## Description

Ce projet implémente un framework d'acteurs distribué inspiré d'Akka, utilisant Spring Boot et Spring Cloud Eureka. Le framework est générique et réutilisable, et est démontré via une application de gestion de matchs NBA en temps réel.

## Architecture

### Structure du Projet

```
actor-framework-project/
├── actor-framework-core/      # Framework générique d'acteurs
├── eureka-server/             # Serveur de découverte de services
├── nba-player-service/        # Microservice de gestion des joueurs
├── nba-team-service/          # Microservice de gestion des équipes
└── nba-game-service/          # Microservice de gestion des matchs
```

### Composants du Framework

1. **ActorSystem** : Gère le cycle de vie des acteurs et leur exécution
2. **Actor** : Interface de base pour tous les acteurs
3. **ActorRef** : Référence vers un acteur (local ou distant)
4. **Message** : Système de messagerie entre acteurs
5. **SupervisorStrategy** : Gestion des erreurs et supervision
6. **ActorLogger** : Système de logs dédié aux acteurs
7. **ActorRegistry** : Registre pour résoudre les références d'acteurs

### Microservices NBA

#### Player Service (Port 8081)
- **PlayerActor** : Gère l'état d'un joueur, ses statistiques et sa forme physique
- Endpoints REST pour créer des joueurs et interagir avec eux

#### Team Service (Port 8082)
- **CoachActor** : Prend des décisions tactiques, effectue des changements
- Endpoints REST pour gérer les équipes et les stratégies

#### Game Service (Port 8083)
- **ScoreboardActor** : Met à jour le score, gère le chronomètre
- Endpoints REST pour créer et gérer les matchs

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0

## Installation et Exécution

### 1. Compiler le projet

```bash
mvn clean install
```

### 2. Démarrer Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Eureka sera accessible sur http://localhost:8761

### 3. Démarrer les microservices (dans des terminaux séparés)

```bash
# Terminal 1 - Player Service
cd nba-player-service
mvn spring-boot:run

# Terminal 2 - Team Service
cd nba-team-service
mvn spring-boot:run

# Terminal 3 - Game Service
cd nba-game-service
mvn spring-boot:run
```

## Interface Web

Une interface web moderne est disponible dans le dossier `nba-frontend/`. 

**Pour l'utiliser :**
1. Ouvrez `nba-frontend/index.html` dans votre navigateur
2. Ou servez-le avec un serveur HTTP : `python -m http.server 8000` (depuis nba-frontend)
3. L'interface permet de créer des joueurs, équipes, matchs et visualiser le score en temps réel !

Voir `README_FRONTEND.md` pour plus de détails.

## Utilisation (APIs REST)

### Créer un joueur

```bash
curl -X POST http://localhost:8081/api/players/create \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "name": "LeBron James",
    "position": "SF",
    "jerseyNumber": 23,
    "teamId": "LAL"
  }'
```

### Créer un coach

```bash
curl -X POST http://localhost:8082/api/teams/create \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LAL",
    "name": "Lakers",
    "city": "Los Angeles",
    "coachId": "coach1"
  }'
```

### Créer un match

```bash
curl -X POST http://localhost:8083/api/games/create \
  -H "Content-Type: application/json" \
  -d '{
    "id": "game1",
    "homeTeamId": "LAL",
    "awayTeamId": "BOS",
    "status": "SCHEDULED"
  }'
```

### Démarrer un match

```bash
curl -X POST http://localhost:8083/api/games/game1/start
```

### Enregistrer une action de joueur

```bash
curl -X POST http://localhost:8083/api/games/game1/action \
  -H "Content-Type: application/json" \
  -d '{
    "playerId": "1",
    "action": "SCORE",
    "team": "HOME",
    "points": 2
  }'
```

### Obtenir le score

```bash
curl http://localhost:8083/api/games/game1/score
```

## Fonctionnalités Implémentées

### ✅ Framework Générique
- Gestion d'acteurs intra et inter-microservices
- Communication synchrone (`ask`) et asynchrone (`tell`)
- Système de supervision avec stratégies personnalisables
- Scalabilité (création/destruction dynamique d'acteurs)
- Système de logs structurés par acteur

### ✅ Application NBA
- Gestion des joueurs avec statistiques en temps réel
- Gestion des équipes avec stratégies tactiques
- Gestion des matchs avec score en temps réel
- Communication inter-microservices entre acteurs

### ✅ Technologies Spring Boot
- Spring Cloud Eureka pour la découverte de services
- WebClient pour la communication HTTP réactive
- Actuator pour le monitoring
- Configuration externalisée avec YAML

## Tests

### Exécuter les tests unitaires

```bash
mvn test
```

### Tests disponibles
- Tests du framework (`ActorSystemTest`)
- Tests des acteurs NBA (`PlayerActorTest`)

## Logs

Les logs des acteurs sont écrits dans le répertoire `logs/actors/` avec un fichier par acteur :
- Format : `logs/actors/{actorId}.log`
- Contenu : Toutes les actions de l'acteur (création, messages, erreurs)

## Architecture et Concepts

### Communication entre Acteurs

1. **Intra-microservice** : Utilise `LocalActorRef` avec un pool de threads
2. **Inter-microservice** : Utilise `RemoteActorRef` avec WebClient et Eureka

### Supervision

Le framework implémente un système de supervision hiérarchique :
- **RESTART** : Redémarre l'acteur en cas d'erreur non fatale
- **RESUME** : Continue l'exécution après une erreur de validation
- **STOP** : Arrête l'acteur en cas d'erreur fatale
- **ESCALATE** : Escalade au superviseur parent

### Scalabilité

- Les acteurs peuvent être créés dynamiquement selon la charge
- Le système utilise un pool de threads configurable
- Support de la découverte de services pour la distribution

## Bibliographie

- Akka Documentation: https://akka.io/docs/
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Cloud Documentation: https://spring.io/projects/spring-cloud
- Actor Model: https://en.wikipedia.org/wiki/Actor_model

## Auteurs

Projet réalisé dans le cadre du module JEE, ING2, GI - Septembre 2025

