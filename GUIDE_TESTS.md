# 🧪 Guide de Tests - NBA Actor Framework

Ce guide vous explique comment tester toutes les fonctionnalités de l'application.

## 📋 Table des Matières

1. [Tests via l'Interface Web](#tests-via-linterface-web)
2. [Tests via les APIs REST](#tests-via-les-apis-rest)
3. [Scénarios de Test Complets](#scénarios-de-test-complets)
4. [Vérifications Techniques](#vérifications-techniques)
5. [Tests de Communication Inter-Microservices](#tests-de-communication-inter-microservices)

---

## 🖥️ Tests via l'Interface Web

**URL : http://localhost:8000**

### ✅ Test 1 : Créer des Joueurs

1. **Section "Créer un Joueur"**
   - ID Joueur : `1`
   - Nom : `LeBron James`
   - Position : `SF` (Small Forward)
   - Numéro de maillot : `23`
   - ID Équipe : `LAL`
   - Cliquez sur **"Créer Joueur"**
   - ✅ Vérifiez : Message de succès dans les logs

2. **Créer d'autres joueurs** :
   - Joueur 2 : `Stephen Curry`, `PG`, `30`, équipe `GSW`
   - Joueur 3 : `Kevin Durant`, `PF`, `35`, équipe `BKN`

### ✅ Test 2 : Voir les Statistiques d'un Joueur

1. Dans la section "Voir les Statistiques"
2. Entrez l'ID du joueur : `1`
3. Cliquez sur **"Charger les Stats"**
4. ✅ Vérifiez : Les statistiques s'affichent (points, rebonds, passes, etc.)
5. Activez **"🔄 Auto-rafraîchissement"** pour voir les stats se mettre à jour en temps réel

### ✅ Test 3 : Créer des Équipes

1. **Section "Créer une Équipe"**
   - ID Équipe : `LAL`
   - Nom : `Lakers`
   - Ville : `Los Angeles`
   - ID Coach : `coach1`
   - Cliquez sur **"Créer Équipe"**
   - ✅ Vérifiez : Message de succès

2. **Créer une deuxième équipe** :
   - ID : `BOS`, Nom : `Celtics`, Ville : `Boston`, Coach : `coach2`

3. **Voir les équipes créées** :
   - Cliquez sur **"🔄 Rafraîchir"** dans "Équipes Créées"
   - ✅ Vérifiez : Les deux équipes apparaissent avec leurs statistiques

### ✅ Test 4 : Gérer les Équipes

1. **Sélectionner des joueurs pour une équipe** :
   - Entrez l'ID d'équipe : `LAL`
   - Cliquez sur **"Sélectionner Joueurs"**
   - Entrez : `1,2,3` (IDs des joueurs)
   - ✅ Vérifiez : Message de confirmation

2. **Changer la stratégie** :
   - Cliquez sur **"Changer Stratégie"**
   - Entrez : `FAST_BREAK` ou `DEFENSIVE`
   - ✅ Vérifiez : Message de confirmation

3. **Voir les joueurs de l'équipe** :
   - Cliquez sur **"Voir Joueurs"**
   - ✅ Vérifiez : Liste des joueurs de l'équipe

### ✅ Test 5 : Créer et Gérer un Match

1. **Créer un match** :
   - ID Match : `game1`
   - Équipe Domicile : `LAL`
   - Équipe Visiteur : `BOS`
   - Cliquez sur **"Créer Match"**
   - ✅ Vérifiez : Le match est créé, les noms des équipes apparaissent dans le tableau de score

2. **Démarrer le match** :
   - Cliquez sur **"Démarrer Match"**
   - ✅ Vérifiez : Le statut passe à `IN_PROGRESS`, le score se rafraîchit automatiquement

3. **Enregistrer des actions** :
   - Utilisez les boutons d'actions rapides :
     - **HOME 2pts** : Ajoute 2 points à l'équipe domicile
     - **HOME 3pts** : Ajoute 3 points
     - **AWAY 2pts** : Ajoute 2 points à l'équipe visiteur
     - **Rebond**, **Passe D** : Enregistre d'autres statistiques
   - ✅ Vérifiez : Le score se met à jour automatiquement dans le tableau

4. **Voir le score** :
   - Cliquez sur **"Voir Score"** ou attendez le rafraîchissement automatique
   - ✅ Vérifiez : Score, quart-temps, temps restant affichés

### ✅ Test 6 : Mettre à Jour les Statistiques d'un Joueur

1. Dans la section "Modifier Stats Joueur"
2. ID Joueur : `1`
3. Points : `10`
4. Rebonds : `5`
5. Passes Décisives : `3`
6. Cliquez sur **"Mettre à jour Stats"**
7. ✅ Vérifiez : Les stats sont mises à jour (vérifiez dans "Voir les Statistiques")

### ✅ Test 7 : Terminer un Match

1. Cliquez sur **"Terminer Match"**
2. Confirmez l'action
3. ✅ Vérifiez : 
   - Le statut passe à `FINISHED`
   - Les équipes sont mises à jour (victoires/défaites)
   - Rafraîchissez la liste des équipes pour voir les nouveaux records

---

## 🔌 Tests via les APIs REST

### Prérequis
- Tous les services doivent être démarrés
- Utilisez `curl` (PowerShell/CMD) ou Postman

### ✅ Test 1 : Player Service (Port 8081)

#### Créer un joueur
```powershell
curl -X POST http://localhost:8081/api/players/create `
  -H "Content-Type: application/json" `
  -d '{\"id\": \"1\", \"name\": \"LeBron James\", \"position\": \"SF\", \"jerseyNumber\": 23, \"teamId\": \"LAL\"}'
```

#### Obtenir les informations d'un joueur
```powershell
curl http://localhost:8081/api/players/player-1/info
```

#### Faire rejoindre un joueur à un match
```powershell
curl -X POST http://localhost:8081/api/players/player-1/join-game
```

#### Effectuer une action
```powershell
curl -X POST http://localhost:8081/api/players/player-1/action `
  -H "Content-Type: application/json" `
  -d '{\"action\": \"SCORE\"}'
```

### ✅ Test 2 : Team Service (Port 8082)

#### Créer une équipe
```powershell
curl -X POST http://localhost:8082/api/teams/create `
  -H "Content-Type: application/json" `
  -d '{\"id\": \"LAL\", \"name\": \"Lakers\", \"city\": \"Los Angeles\", \"coachId\": \"coach1\"}'
```

#### Obtenir toutes les équipes
```powershell
curl http://localhost:8082/api/teams
```

#### Obtenir une équipe spécifique
```powershell
curl http://localhost:8082/api/teams/LAL
```

#### Vérifier si une équipe existe
```powershell
curl http://localhost:8082/api/teams/LAL/exists
```

#### Sélectionner des joueurs (via le coach)
```powershell
curl -X POST http://localhost:8082/api/teams/LAL/coach/select-players `
  -H "Content-Type: application/json" `
  -d '[\"1\", \"2\", \"3\", \"4\", \"5\"]'
```

#### Changer la stratégie
```powershell
curl -X POST http://localhost:8082/api/teams/LAL/coach/strategy `
  -H "Content-Type: application/json" `
  -d '{\"strategy\": \"FAST_BREAK\"}'
```

### ✅ Test 3 : Game Service (Port 8083)

#### Créer un match
```powershell
curl -X POST http://localhost:8083/api/games/create `
  -H "Content-Type: application/json" `
  -d '{\"id\": \"game1\", \"homeTeamId\": \"LAL\", \"awayTeamId\": \"BOS\", \"status\": \"SCHEDULED\"}'
```

#### Démarrer un match
```powershell
curl -X POST http://localhost:8083/api/games/game1/start
```

#### Obtenir le score
```powershell
curl http://localhost:8083/api/games/game1/score
```

#### Enregistrer une action
```powershell
curl -X POST http://localhost:8083/api/games/game1/action `
  -H "Content-Type: application/json" `
  -d '{\"playerId\": \"1\", \"action\": \"SCORE\", \"team\": \"HOME\", \"points\": 2}'
```

#### Mettre à jour les stats d'un joueur
```powershell
curl -X POST http://localhost:8083/api/games/game1/update-player-stats `
  -H "Content-Type: application/json" `
  -d '{\"playerId\": \"1\", \"points\": 10, \"rebounds\": 5, \"assists\": 3}'
```

#### Terminer un match
```powershell
curl -X POST http://localhost:8083/api/games/game1/end
```

---

## 🎬 Scénarios de Test Complets

### Scénario 1 : Match Complet Simple

1. **Créer 2 équipes** :
   - Équipe HOME : `LAL` (Lakers)
   - Équipe AWAY : `BOS` (Celtics)

2. **Créer 2 joueurs** :
   - Joueur 1 : `LeBron James`, équipe `LAL`
   - Joueur 2 : `Jayson Tatum`, équipe `BOS`

3. **Créer un match** :
   - Match ID : `game1`
   - HOME : `LAL`, AWAY : `BOS`

4. **Démarrer le match**

5. **Enregistrer des actions** :
   - HOME marque 2 points (Joueur 1)
   - AWAY marque 3 points (Joueur 2)
   - HOME marque 2 points (Joueur 1)
   - HOME rebond (Joueur 1)

6. **Vérifier** :
   - Score HOME : 4, AWAY : 3
   - Stats du Joueur 1 : 4 points, 1 rebond

7. **Terminer le match**

8. **Vérifier** :
   - LAL a gagné (1 victoire)
   - BOS a perdu (1 défaite)

### Scénario 2 : Test de Communication Inter-Microservices

1. **Créer un joueur** avec une équipe qui n'existe pas encore :
   - Joueur ID : `10`, Équipe : `MIA`
   - ✅ Vérifiez : L'équipe est créée automatiquement

2. **Créer un match** avec cette équipe :
   - HOME : `MIA`, AWAY : `LAL`
   - ✅ Vérifiez : Le match est créé sans erreur

3. **Enregistrer une action** :
   - Action du Joueur 10 dans le match
   - ✅ Vérifiez : Les stats du joueur sont mises à jour (via communication inter-services)

### Scénario 3 : Test de Validation

1. **Tenter de créer un match avec une équipe inexistante** :
   - HOME : `XXX` (n'existe pas)
   - ✅ Vérifiez : Erreur avec message explicite

2. **Tenter de créer une équipe qui existe déjà** :
   - Créer `LAL` deux fois
   - ✅ Vérifiez : Erreur "Team already exists"

---

## 🔍 Vérifications Techniques

### Vérifier Eureka Dashboard

1. Ouvrez : http://localhost:8761
2. ✅ Vérifiez que tous les services sont enregistrés :
   - `NBA-PLAYER-SERVICE` (UP)
   - `NBA-TEAM-SERVICE` (UP)
   - `NBA-GAME-SERVICE` (UP)

### Vérifier les Logs des Acteurs

Les logs sont dans `logs/actors/` :

1. **Logs d'un joueur** : `logs/actors/player-1.log`
   - ✅ Vérifiez : Création, messages reçus, stats mises à jour

2. **Logs d'un coach** : `logs/actors/coach-LAL.log`
   - ✅ Vérifiez : Création, sélection de joueurs, changements de stratégie

3. **Logs d'un match** : `logs/actors/scoreboard-game1.log`
   - ✅ Vérifiez : Création, démarrage, actions enregistrées, score mis à jour

### Vérifier les Logs des Services

Dans les terminaux où les services tournent, vérifiez :
- ✅ Pas d'erreurs de compilation
- ✅ Messages de démarrage réussis
- ✅ Messages de communication inter-services

---

## 🌐 Tests de Communication Inter-Microservices

### Test 1 : Player → Team (Création automatique d'équipe)

1. Créez un joueur avec `teamId: "NEW_TEAM"` (équipe qui n'existe pas)
2. ✅ Vérifiez dans les logs du Player Service : Message indiquant la création automatique
3. ✅ Vérifiez dans Team Service : L'équipe `NEW_TEAM` existe maintenant

### Test 2 : Game → Player (Mise à jour des stats)

1. Créez un match et démarrez-le
2. Enregistrez une action pour un joueur
3. ✅ Vérifiez dans les logs du Game Service : Communication avec Player Service
4. ✅ Vérifiez dans les logs du Player Service : Réception du message de mise à jour
5. ✅ Vérifiez les stats du joueur : Elles sont mises à jour

### Test 3 : Game → Team (Validation des équipes)

1. Tentez de créer un match avec une équipe inexistante
2. ✅ Vérifiez : Le Game Service communique avec Team Service pour valider
3. ✅ Vérifiez : Erreur retournée si l'équipe n'existe pas

---

## ✅ Checklist de Tests

### Tests Fonctionnels
- [ ] Créer un joueur
- [ ] Voir les stats d'un joueur
- [ ] Créer une équipe
- [ ] Voir les équipes créées
- [ ] Sélectionner des joueurs pour une équipe
- [ ] Changer la stratégie d'une équipe
- [ ] Créer un match
- [ ] Démarrer un match
- [ ] Enregistrer des actions (paniers, rebonds, passes)
- [ ] Voir le score en temps réel
- [ ] Mettre à jour les stats d'un joueur
- [ ] Terminer un match
- [ ] Vérifier les victoires/défaites des équipes

### Tests Techniques
- [ ] Tous les services sont enregistrés dans Eureka
- [ ] Les logs des acteurs sont créés
- [ ] Communication inter-microservices fonctionne
- [ ] Validation des données (équipes inexistantes, etc.)
- [ ] Création automatique d'équipe si nécessaire

### Tests d'Intégration
- [ ] Scénario complet de match
- [ ] Communication Player ↔ Game
- [ ] Communication Game ↔ Team
- [ ] Communication Player ↔ Team
- [ ] Auto-rafraîchissement des stats
- [ ] Auto-rafraîchissement du score

---

## 🐛 Tests de Gestion d'Erreurs

### Test 1 : Joueur inexistant
```powershell
curl http://localhost:8081/api/players/player-999/info
```
✅ Attendu : 404 Not Found

### Test 2 : Équipe inexistante pour un match
```powershell
curl -X POST http://localhost:8083/api/games/create `
  -H "Content-Type: application/json" `
  -d '{\"id\": \"game2\", \"homeTeamId\": \"INEXISTANT\", \"awayTeamId\": \"LAL\", \"status\": \"SCHEDULED\"}'
```
✅ Attendu : Erreur avec message explicite

### Test 3 : Match inexistant
```powershell
curl http://localhost:8083/api/games/game-inexistant/score
```
✅ Attendu : 404 Not Found

---

## 📊 Tests de Performance (Optionnel)

1. **Créer plusieurs joueurs rapidement** (10-20 joueurs)
   - ✅ Vérifiez : Tous sont créés sans erreur

2. **Enregistrer plusieurs actions rapidement**
   - ✅ Vérifiez : Le score se met à jour correctement

3. **Auto-rafraîchissement des stats**
   - ✅ Vérifiez : Les stats se mettent à jour toutes les 3 secondes

---

## 💡 Conseils pour les Tests

1. **Commencez simple** : Testez d'abord via l'interface web (plus facile)
2. **Vérifiez les logs** : Regardez les fichiers de logs pour comprendre ce qui se passe
3. **Testez les erreurs** : Vérifiez que les validations fonctionnent
4. **Testez la communication** : Créez des scénarios qui nécessitent la communication entre services
5. **Utilisez Postman** : Importez `postman-collection.json` pour tester les APIs facilement

---

## 🎯 Tests Prioritaires (Minimum à faire)

Si vous avez peu de temps, testez au minimum :

1. ✅ Créer 2 joueurs
2. ✅ Créer 2 équipes
3. ✅ Créer un match
4. ✅ Démarrer le match
5. ✅ Enregistrer 3-4 actions
6. ✅ Vérifier le score
7. ✅ Vérifier les stats d'un joueur
8. ✅ Terminer le match
9. ✅ Vérifier Eureka Dashboard

Ces tests couvrent les fonctionnalités principales et la communication inter-microservices.

