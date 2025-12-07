# Guide d'Utilisation du Front-End - Explication Technique

## 🎯 Workflow de Test Recommandé

### Étape 1 : Créer un Joueur

**Action dans le front-end :**
1. Remplissez le formulaire "Créer un Joueur"
   - ID: `1`
   - Nom: `LeBron James`
   - Position: `SF` (Small Forward)
   - Numéro: `23`
   - Équipe: `LAL`

2. Cliquez sur "Créer Joueur"

**Ce qui se passe techniquement :**

```
Front-End (JavaScript)
  ↓
  POST http://localhost:8081/api/players/create
  Body: { id: "1", name: "LeBron James", ... }
  ↓
PlayerController (nba-player-service)
  ↓
  Crée une instance de PlayerActor
  actorId = "player-1"
  ↓
ActorSystem.createActor(PlayerActor)
  ↓
  - Enregistre l'acteur dans le système
  - Appelle actor.preStart()
  - Crée un LocalActorRef
  - Enregistre dans ActorRegistry
  - ActorLogger écrit dans logs/actors/player-1.log
  ↓
Réponse JSON: { actorId: "player-1", path: "/user/PlayerActor/player-1" }
  ↓
Front-End affiche: "✅ Joueur créé: LeBron James (player-1)"
```

**Résultat :**
- Un acteur `PlayerActor` est créé dans le microservice Player
- L'acteur a un état (player, stats, fatigue, etc.)
- Un fichier de log est créé : `logs/actors/player-1.log`
- L'acteur est prêt à recevoir des messages

---

### Étape 2 : Créer une Équipe

**Action dans le front-end :**
1. Remplissez le formulaire "Créer une Équipe"
   - ID: `LAL`
   - Nom: `Lakers`
   - Ville: `Los Angeles`
   - Coach: `coach1`

2. Cliquez sur "Créer Équipe"

**Ce qui se passe techniquement :**

```
Front-End
  ↓
  POST http://localhost:8082/api/teams/create
  Body: { id: "LAL", name: "Lakers", coachId: "coach1", ... }
  ↓
TeamController (nba-team-service)
  ↓
  Crée une instance de CoachActor
  actorId = "coach-LAL"
  ↓
ActorSystem.createActor(CoachActor)
  ↓
  - CoachActor créé avec ActorRegistry injecté
  - Permet au coach de communiquer avec les joueurs (autre microservice)
  ↓
Réponse: { coachActorId: "coach-LAL", path: "/user/CoachActor/coach-LAL" }
```

**Résultat :**
- Un acteur `CoachActor` est créé dans le microservice Team
- Le coach peut maintenant sélectionner des joueurs et gérer la stratégie

---

### Étape 3 : Sélectionner des Joueurs pour l'Équipe

**Action dans le front-end :**
1. Dans la section "Équipe", entrez l'ID d'équipe : `LAL`
2. Cliquez sur "Sélectionner Joueurs"
3. Entrez les IDs : `1,2,3,4,5` (séparés par des virgules)

**Ce qui se passe techniquement :**

```
Front-End
  ↓
  POST http://localhost:8082/api/teams/LAL/coach/select-players
  Body: ["1", "2", "3", "4", "5"]
  ↓
TeamController
  ↓
  Résout l'acteur: ActorRegistry.resolveActor("/user/CoachActor/coach-LAL")
  ↓
  Crée un Message:
    messageType: "SELECT_PLAYERS"
    payload: ["1", "2", "3", "4", "5"]
    requiresResponse: true
  ↓
  Communication SYNCHRONE (ask):
  coachRef.ask(message, 5000)
  ↓
CoachActor.receive(message)
  ↓
  onReceive() traite "SELECT_PLAYERS"
  activePlayers = ["1", "2", "3", "4", "5"]
  ↓
  Retourne: "Players selected: 5"
  ↓
Réponse au front-end: "Players selected: 5"
```

**Résultat :**
- Le coach a maintenant 5 joueurs actifs
- Communication **synchrone** (ask) : le front-end attend la réponse

---

### Étape 4 : Créer un Match

**Action dans le front-end :**
1. Remplissez le formulaire "Créer un Match"
   - ID: `game1`
   - Domicile: `LAL`
   - Visiteur: `BOS`

2. Cliquez sur "Créer Match"

**Ce qui se passe techniquement :**

```
Front-End
  ↓
  POST http://localhost:8083/api/games/create
  Body: { id: "game1", homeTeamId: "LAL", awayTeamId: "BOS", status: "SCHEDULED" }
  ↓
GameController (nba-game-service)
  ↓
  Crée une instance de ScoreboardActor
  actorId = "scoreboard-game1"
  ↓
ActorSystem.createActor(ScoreboardActor)
  ↓
  - ScoreboardActor créé avec Game et ActorRegistry
  - ActorRegistry permet de communiquer avec les joueurs (autre microservice)
  ↓
Réponse: { actorId: "scoreboard-game1", path: "/user/ScoreboardActor/scoreboard-game1" }
  ↓
Front-End met à jour le tableau de score avec les noms d'équipes
```

**Résultat :**
- Un acteur `ScoreboardActor` est créé dans le microservice Game
- Le match est prêt à démarrer

---

### Étape 5 : Démarrer le Match

**Action dans le front-end :**
1. Cliquez sur "Démarrer Match"

**Ce qui se passe techniquement :**

```
Front-End
  ↓
  POST http://localhost:8083/api/games/game1/start
  ↓
GameController
  ↓
  Résout: ActorRegistry.resolveActor("/user/ScoreboardActor/scoreboard-game1")
  ↓
  Message: { messageType: "START_GAME", requiresResponse: true }
  ↓
  Communication SYNCHRONE (ask)
  ↓
ScoreboardActor.receive(message)
  ↓
  onReceive() traite "START_GAME"
  - game.status = "IN_PROGRESS"
  - gameRunning = true
  - Démarre un ScheduledExecutorService (chronomètre)
  - Le chronomètre décrémente timeRemaining chaque seconde
  ↓
  Retourne: "Game started"
  ↓
Front-End:
  - Change le statut à "IN_PROGRESS"
  - Démarre le rafraîchissement automatique du score (toutes les 2 secondes)
```

**Résultat :**
- Le match est en cours
- Le chronomètre tourne dans l'acteur
- Le front-end rafraîchit le score automatiquement

---

### Étape 6 : Enregistrer une Action (Panier)

**Action dans le front-end :**
1. Cliquez sur "Panier 2pts" dans le tableau de score

**Ce qui se passe techniquement :**

```
Front-End
  ↓
  POST http://localhost:8083/api/games/game1/action
  Body: {
    playerId: "1",
    action: "SCORE",
    team: "HOME",
    points: 2
  }
  ↓
GameController
  ↓
  Message: { messageType: "PLAYER_ACTION", payload: {...}, requiresResponse: false }
  ↓
  Communication ASYNCHRONE (tell)
  scoreboardRef.tell(message)
  ↓
ScoreboardActor.receive(message)
  ↓
  onReceive() traite "PLAYER_ACTION"
  ↓
  [COMMUNICATION INTER-MICROSERVICES]
  ActorRegistry.resolveActor("/nba-player-service/user/PlayerActor/player-1")
  ↓
  Résolution via Eureka:
    - ActorRegistry vérifie Eureka pour trouver nba-player-service
    - Crée un RemoteActorRef
    - Utilise WebClient pour HTTP
  ↓
  POST http://nba-player-service/api/actors/message
  (via RemoteActorRef)
  ↓
ActorController (nba-player-service)
  ↓
  Reçoit le message et résout l'acteur local
  ↓
PlayerActor.receive(message)
  messageType: "PERFORM_ACTION"
  payload: "SCORE"
  ↓
  onReceive() traite "PERFORM_ACTION"
  - Met à jour les stats (points += 2)
  - Augmente la fatigue
  - Log dans logs/actors/player-1.log
  ↓
  Retourne: "Action performed: SCORE"
  ↓
ScoreboardActor continue:
  - Met à jour le score: homeScore += 2
  - Log dans logs/actors/scoreboard-game1.log
  ↓
Front-End rafraîchit automatiquement le score (toutes les 2 secondes)
  GET http://localhost:8083/api/games/game1/score
  ↓
  Réponse: { homeScore: 2, awayScore: 0, quarter: 1, ... }
  ↓
  Front-End met à jour l'affichage du score
```

**Résultat :**
- Le joueur (dans Player Service) a ses stats mises à jour
- Le score (dans Game Service) est mis à jour
- **Communication inter-microservices** réussie via Eureka et WebClient
- Les logs sont écrits dans les fichiers respectifs

---

## 🔄 Types de Communication

### 1. Communication Synchrone (`ask`)
- Le front-end attend une réponse
- Utilisé pour : créer, démarrer, obtenir des infos
- Timeout : 5 secondes

### 2. Communication Asynchrone (`tell`)
- Le front-end n'attend pas de réponse
- Utilisé pour : actions de jeu, notifications
- Fire-and-forget

### 3. Communication Inter-Microservices
- Via `RemoteActorRef` et `ActorRegistry`
- Utilise Eureka pour découvrir les services
- Utilise WebClient pour HTTP réactif
- Exemple : ScoreboardActor → PlayerActor (différents services)

---

## 📊 Flux Complet d'une Action de Jeu

```
Front-End (Interface)
    ↓
    POST /api/games/game1/action
    ↓
Game Service (Port 8083)
    ├─ GameController reçoit la requête
    ├─ Résout ScoreboardActor (local)
    └─ ScoreboardActor traite l'action
        ├─ Met à jour le score localement
        └─ Communique avec Player Service
            ↓
            ActorRegistry.resolveActor()
            ↓
            Eureka Discovery
            ↓
            RemoteActorRef
            ↓
            WebClient HTTP
            ↓
Player Service (Port 8081)
    ├─ ActorController reçoit le message
    ├─ Résout PlayerActor (local)
    └─ PlayerActor met à jour ses stats
        └─ Log dans logs/actors/player-1.log
    ↓
    Réponse HTTP
    ↓
Game Service reçoit la confirmation
    └─ Log dans logs/actors/scoreboard-game1.log
    ↓
Front-End rafraîchit le score
    GET /api/games/game1/score
    ↓
    Affiche le nouveau score
```

---

## 🎮 Actions Disponibles dans le Front-End

### Section Joueur
- **Créer Joueur** : Crée un PlayerActor
- Les logs s'affichent en bas de la carte

### Section Équipe
- **Créer Équipe** : Crée un CoachActor
- **Sélectionner Joueurs** : Envoie un message au CoachActor
- **Changer Stratégie** : Met à jour la stratégie du coach

### Section Match
- **Créer Match** : Crée un ScoreboardActor
- **Démarrer Match** : Active le chronomètre dans l'acteur
- **Voir Score** : Récupère l'état actuel du match

### Tableau de Score
- **Panier 2pts** : Action SCORE avec 2 points
- **Panier 3pts** : Action SCORE avec 3 points
- **Rebond** : Action REBOUND
- **Passe D** : Action ASSIST

Chaque action déclenche une communication inter-microservices !

---

## 🔍 Vérifications Techniques

### Vérifier les Logs des Acteurs
Les fichiers sont dans `logs/actors/` :
- `player-1.log` : Toutes les actions du joueur
- `coach-LAL.log` : Actions du coach
- `scoreboard-game1.log` : Événements du match

### Vérifier Eureka
http://localhost:8761
- Les 3 services doivent être UP
- Chaque service peut découvrir les autres

### Vérifier les APIs Directement
```bash
# Voir les infos d'un joueur
curl http://localhost:8081/api/players/player-1/info

# Voir le score
curl http://localhost:8083/api/games/game1/score
```

---

## 🎯 Test Complet Recommandé

1. **Créer 2 joueurs** : IDs 1 et 2
2. **Créer une équipe** : LAL
3. **Sélectionner les joueurs** : 1,2
4. **Créer un match** : game1 (LAL vs BOS)
5. **Démarrer le match**
6. **Enregistrer plusieurs actions** :
   - Panier 2pts (joueur 1)
   - Panier 3pts (joueur 2)
   - Rebond (joueur 1)
7. **Observer** :
   - Le score se met à jour automatiquement
   - Les logs dans les fichiers
   - La communication entre services dans Eureka

Voilà ! Vous testez maintenant un système d'acteurs distribué complet ! 🎉

