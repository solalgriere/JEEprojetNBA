# 🔍 Guide pour Vérifier si les Stats se Mettent à Jour

## Problème
Les actions pendant le match ne mettent pas à jour les stats du joueur dans l'interface.

## ✅ Vérifications à Faire

### 1. Vérifier les Logs du ScoreboardActor

**Fichier** : `logs/actors/scoreboard-game1.log` (ou le nom de votre match)

**Cherchez** :
```
✅ Sent PERFORM_ACTION message to player X: action=SCORE, points=2
```

**Si vous voyez** :
- ✅ Le message est envoyé → Le problème est dans la communication ou le PlayerActor
- ❌ "Could not resolve player actor" → Le joueur n'existe pas ou n'est pas accessible

---

### 2. Vérifier les Logs du PlayerActor

**Fichier** : `logs/actors/player-X.log` (X = ID du joueur, ex: player-1.log)

**Cherchez** :
```
🎮 Player player-X received PERFORM_ACTION: {action=SCORE, points=2}
🏀 Player player-X scored 2 points (was: 0, now: 2)
✅ Player player-X stats updated after SCORE: Points=2, Rebounds=0, ...
```

**Si vous voyez** :
- ✅ Les logs → Les stats sont mises à jour dans l'acteur
- ❌ Pas de logs → Le message n'arrive pas au joueur

---

### 3. Vérifier dans le Frontend

**Ouvrez la console du navigateur** (Ctrl+Shift+J ou clic droit → Inspecter → Console)

**Cherchez** :
```
Player info reçu: {...}
Stats reçues: {points: 2, rebounds: 0, ...}
```

**Si les stats sont à 0 dans la console** :
- Le problème vient de la mise à jour dans l'acteur
- Vérifiez les logs du PlayerActor (étape 2)

**Si les stats sont correctes dans la console mais pas à l'écran** :
- Le problème vient de l'affichage
- Rafraîchissez la page (F5)

---

## 🔧 Solutions

### Solution 1 : Le message n'arrive pas au joueur

**Symptôme** : Pas de logs dans `player-X.log`

**Causes possibles** :
1. Le joueur n'a pas été créé
2. Le service Player n'est pas accessible
3. Eureka ne trouve pas le service

**Actions** :
1. Vérifiez que le joueur existe : `GET http://localhost:8081/api/players/player-1/info`
2. Vérifiez Eureka : http://localhost:8761 → Le service `nba-player-service` doit être enregistré
3. Redémarrez les services si nécessaire

---

### Solution 2 : Les stats sont mises à jour mais pas affichées

**Symptôme** : Logs OK dans `player-X.log`, mais stats à 0 dans l'interface

**Actions** :
1. Rafraîchissez la page (F5)
2. Cliquez à nouveau sur "Voir Stats du Joueur"
3. Attendez 3 secondes (rafraîchissement automatique)

---

### Solution 3 : Communication inter-microservices

**Symptôme** : "Could not resolve player actor" dans les logs

**Actions** :
1. Vérifiez que tous les services sont démarrés
2. Vérifiez Eureka : http://localhost:8761
3. Vérifiez les logs pour les erreurs de connexion

---

## 📝 Test Complet

1. **Créez un joueur** (ID: `1`)
2. **Vérifiez qu'il existe** : Cliquez sur "Voir Stats du Joueur" avec `1`
3. **Créez un match** et démarrez-le
4. **Faites une action** : Cliquez sur "HOME 2pts" avec l'ID `1` dans "ID Joueur pour Actions"
5. **Attendez 2-3 secondes**
6. **Vérifiez les stats** : Elles devraient se mettre à jour automatiquement

---

## 🐛 Debug Avancé

### Vérifier manuellement les stats via l'API

```bash
# Récupérer les stats du joueur
curl http://localhost:8081/api/players/player-1/info
```

**Résultat attendu** :
```json
{
  "actorId": "player-1",
  "player": {...},
  "stats": {
    "points": 2,
    "rebounds": 0,
    ...
  }
}
```

Si les stats sont correctes dans l'API mais pas dans l'interface, c'est un problème d'affichage.

---

## ✅ Checklist

- [ ] Le joueur a été créé
- [ ] Le match a été créé et démarré
- [ ] L'ID du joueur est correct dans "ID Joueur pour Actions"
- [ ] Les logs du ScoreboardActor montrent que le message est envoyé
- [ ] Les logs du PlayerActor montrent que le message est reçu
- [ ] Les stats sont mises à jour dans les logs
- [ ] L'interface affiche les stats (case "📊 Statistiques")
- [ ] Le rafraîchissement automatique fonctionne (toutes les 3 secondes)


