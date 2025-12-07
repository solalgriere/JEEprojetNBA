# Guide de Démarrage Complet - NBA Actor Framework

## 📋 Prérequis

Avant de commencer, assurez-vous d'avoir :
- ✅ Java 17 ou supérieur installé
- ✅ Maven 3.6+ installé
- ✅ Terminal/Invite de commande ouvert

Vérifiez avec :
```bash
java -version
mvn -version
```

---

## 🚀 ÉTAPE 1 : Compiler le Projet

**Ouvrez un terminal à la racine du projet** (`H:\Documents\Exam`)

```bash
mvn clean install
```

⏱️ **Temps estimé** : 2-5 minutes

✅ **Vous devriez voir** : `BUILD SUCCESS` à la fin

---

## 🚀 ÉTAPE 2 : Démarrer Eureka Server

**Ouvrez un NOUVEAU terminal** (gardez le premier ouvert)

```bash
cd H:\Documents\Exam\eureka-server
mvn spring-boot:run
```

⏱️ **Attendez** : 30-60 secondes jusqu'à voir :
```
Started EurekaServerApplication in X.XXX seconds
```

✅ **Vérification** : Ouvrez http://localhost:8761 dans votre navigateur
- Vous devriez voir le dashboard Eureka avec "Instances currently registered with Eureka"

🔴 **NE FERMEZ PAS CE TERMINAL** - Laissez Eureka tourner

---

## 🚀 ÉTAPE 3 : Démarrer Player Service

**Ouvrez un NOUVEAU terminal** (3ème terminal)

```bash
cd H:\Documents\Exam\nba-player-service
mvn spring-boot:run
```

⏱️ **Attendez** : 30-60 secondes jusqu'à voir :
```
Started PlayerServiceApplication in X.XXX seconds
```

✅ **Vérification** : 
- Dans le terminal Eureka (terminal 2), vous devriez voir `nba-player-service` apparaître
- Ou vérifiez http://localhost:8761 - le service devrait être listé

🔴 **NE FERMEZ PAS CE TERMINAL**

---

## 🚀 ÉTAPE 4 : Démarrer Team Service

**Ouvrez un NOUVEAU terminal** (4ème terminal)

```bash
cd H:\Documents\Exam\nba-team-service
mvn spring-boot:run
```

⏱️ **Attendez** : 30-60 secondes jusqu'à voir :
```
Started TeamServiceApplication in X.XXX seconds
```

✅ **Vérification** : 
- Dans Eureka (http://localhost:8761), vous devriez voir `nba-team-service`

🔴 **NE FERMEZ PAS CE TERMINAL**

---

## 🚀 ÉTAPE 5 : Démarrer Game Service

**Ouvrez un NOUVEAU terminal** (5ème terminal)

```bash
cd H:\Documents\Exam\nba-game-service
mvn spring-boot:run
```

⏱️ **Attendez** : 30-60 secondes jusqu'à voir :
```
Started GameServiceApplication in X.XXX seconds
```

✅ **Vérification** : 
- Dans Eureka (http://localhost:8761), vous devriez voir les 3 services :
  - nba-player-service
  - nba-team-service
  - nba-game-service

🔴 **NE FERMEZ PAS CE TERMINAL**

---

## 🎯 ÉTAPE 6 : Ouvrir l'Interface Web

**Option A : Ouvrir directement**

1. Naviguez vers : `H:\Documents\Exam\nba-frontend\`
2. Double-cliquez sur `index.html`
3. L'interface s'ouvre dans votre navigateur

**Option B : Servir avec un serveur HTTP** (recommandé si CORS)

**Ouvrez un NOUVEAU terminal** (6ème terminal)

```bash
cd H:\Documents\Exam\nba-frontend
python -m http.server 8000
```

Puis ouvrez dans votre navigateur : http://localhost:8000

---

## ✅ Vérification Finale

Vous devriez avoir :

1. ✅ **Eureka Dashboard** : http://localhost:8761
   - 3 services enregistrés (player, team, game)

2. ✅ **Interface Web** : http://localhost:8000 (ou fichier index.html)
   - Dashboard fonctionnel avec 4 cartes

3. ✅ **5 Terminaux ouverts** :
   - Terminal 1 : Compilation (peut être fermé)
   - Terminal 2 : Eureka Server (DOIT rester ouvert)
   - Terminal 3 : Player Service (DOIT rester ouvert)
   - Terminal 4 : Team Service (DOIT rester ouvert)
   - Terminal 5 : Game Service (DOIT rester ouvert)
   - Terminal 6 : Serveur HTTP (si utilisé, DOIT rester ouvert)

---

## 🎮 Test Rapide dans l'Interface Web

1. **Créer un joueur** :
   - ID: `1`
   - Nom: `LeBron James`
   - Position: `SF`
   - Numéro: `23`
   - Équipe: `LAL`
   - Cliquez "Créer Joueur"
   - ✅ Vous devriez voir un message vert dans les logs

2. **Créer une équipe** :
   - ID: `LAL`
   - Nom: `Lakers`
   - Ville: `Los Angeles`
   - Coach: `coach1`
   - Cliquez "Créer Équipe"
   - ✅ Message de succès

3. **Créer un match** :
   - ID: `game1`
   - Domicile: `LAL`
   - Visiteur: `BOS`
   - Cliquez "Créer Match"
   - ✅ Le tableau de score se met à jour

4. **Démarrer le match** :
   - Cliquez "Démarrer Match"
   - ✅ Le statut passe à "IN_PROGRESS"

5. **Enregistrer une action** :
   - Cliquez "Panier 2pts"
   - ✅ Le score se met à jour automatiquement

---

## 🛑 Pour Arrêter Tout

Dans chaque terminal, appuyez sur :
```
Ctrl + C
```

**Ordre recommandé** :
1. Interface web (si serveur HTTP)
2. Game Service
3. Team Service
4. Player Service
5. Eureka Server (en dernier)

---

## ⚠️ Problèmes Courants

### Erreur "Port already in use"
- Un service est déjà en cours d'exécution
- Fermez les processus Java ou changez le port dans `application.yml`

### Erreur "Connection refused" dans l'interface
- Vérifiez que tous les services sont démarrés
- Vérifiez Eureka : http://localhost:8761

### CORS Error dans la console du navigateur
- Utilisez l'Option B (serveur HTTP) au lieu d'ouvrir directement le fichier

### Service ne s'enregistre pas dans Eureka
- Attendez 30 secondes supplémentaires
- Vérifiez que Eureka est démarré en premier
- Vérifiez les logs pour les erreurs

---

## 📊 Résumé des URLs

| Service | URL | Description |
|---------|-----|-------------|
| Eureka | http://localhost:8761 | Dashboard de services |
| Player API | http://localhost:8081 | API REST joueurs |
| Team API | http://localhost:8082 | API REST équipes |
| Game API | http://localhost:8083 | API REST matchs |
| Interface Web | http://localhost:8000 | Dashboard NBA |

---

## 🎉 C'est Prêt !

Vous pouvez maintenant utiliser l'interface web pour créer des joueurs, équipes, matchs et voir le système d'acteurs en action !

