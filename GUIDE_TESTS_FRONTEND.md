# 🖥️ Guide de Tests - Interface Web (Frontend)

**URL : http://localhost:8000**

Ce guide vous explique étape par étape comment tester toutes les fonctionnalités via l'interface web.

---

## 📋 Prérequis

✅ Tous les services doivent être démarrés :
- Eureka Server (port 8761)
- Player Service (port 8081)
- Team Service (port 8082)
- Game Service (port 8083)
- Serveur web frontend (port 8000)

---

## 🎯 ÉTAPE 1 : Créer des Joueurs

### Test 1.1 : Créer le premier joueur

1. Dans la section **"👤 Créer un Joueur"** (carte de gauche)
2. Remplissez le formulaire :
   - **ID Joueur** : `1`
   - **Nom** : `LeBron James`
   - **Position** : Sélectionnez `SF` (Small Forward)
   - **Numéro de maillot** : `23`
   - **ID Équipe** : `LAL`
3. Cliquez sur **"Créer Joueur"**
4. ✅ **Vérification** : 
   - Un message vert apparaît dans la zone de logs en bas : `✅ Joueur créé: LeBron James (player-1)`
   - Pas d'erreur rouge

### Test 1.2 : Créer d'autres joueurs

Créez au moins 3-4 joueurs supplémentaires pour avoir une équipe complète :

**Joueur 2** :
- ID : `2`
- Nom : `Anthony Davis`
- Position : `PF` (Power Forward)
- Numéro : `3`
- Équipe : `LAL`

**Joueur 3** :
- ID : `3`
- Nom : `Jayson Tatum`
- Position : `SF`
- Numéro : `0`
- Équipe : `BOS`

**Joueur 4** :
- ID : `4`
- Nom : `Jaylen Brown`
- Position : `SG` (Shooting Guard)
- Numéro : `7`
- Équipe : `BOS`

✅ **Vérification** : Tous les joueurs sont créés avec succès (messages verts dans les logs)

---

## 🏀 ÉTAPE 2 : Voir les Statistiques d'un Joueur

### Test 2.1 : Charger les stats d'un joueur

1. Dans la section **"📊 Voir les Statistiques"** (sous le formulaire de création)
2. Dans le champ **"ID du Joueur"**, entrez : `1`
   - ⚠️ **Important** : Entrez juste le numéro (1, 2, 3...), pas "player-1"
3. Cliquez sur **"Charger les Stats"**
4. ✅ **Vérification** :
   - Une carte s'affiche avec les statistiques du joueur
   - Vous voyez : Nom, Position, Numéro, Équipe
   - Statistiques : Points, Rebonds, Passes Décisives, Interceptions, Contres, Fatigue

### Test 2.2 : Auto-rafraîchissement des stats

1. Cliquez sur **"🔄 Auto-rafraîchissement"**
2. ✅ **Vérification** :
   - Message dans les logs : `🔄 Auto-rafraîchissement activé (toutes les 3 secondes)`
   - Les stats se rafraîchissent automatiquement toutes les 3 secondes
3. Pour arrêter : Cliquez sur **"⏸️ Arrêter"**

---

## 👔 ÉTAPE 3 : Créer des Équipes

### Test 3.1 : Créer la première équipe

1. Dans la section **"👔 Créer une Équipe"** (carte du milieu)
2. Remplissez le formulaire :
   - **ID Équipe** : `LAL`
   - **Nom** : `Lakers`
   - **Ville** : `Los Angeles`
   - **ID Coach** : `coach1`
3. Cliquez sur **"Créer Équipe"**
4. ✅ **Vérification** :
   - Message vert : `✅ Équipe créée: Lakers (coach-LAL)`
   - L'équipe apparaît dans la liste "Équipes Créées" (après rafraîchissement)

### Test 3.2 : Créer une deuxième équipe

**Équipe 2** :
- ID : `BOS`
- Nom : `Celtics`
- Ville : `Boston`
- Coach : `coach2`

Cliquez sur **"Créer Équipe"**

### Test 3.3 : Voir toutes les équipes créées

1. Cliquez sur le bouton **"🔄 Rafraîchir"** dans la section "📋 Équipes Créées"
2. ✅ **Vérification** :
   - Les deux équipes apparaissent dans une liste
   - Pour chaque équipe, vous voyez :
     - Nom et ID
     - Ville
     - Victoires : 0
     - Défaites : 0

### Test 3.4 : Test d'erreur - Équipe déjà existante

1. Essayez de créer à nouveau l'équipe `LAL` avec les mêmes informations
2. ✅ **Vérification** :
   - Message d'erreur rouge : `❌ Erreur: L'équipe avec l'ID LAL existe déjà`
   - L'équipe n'est pas dupliquée

---

## 🎮 ÉTAPE 4 : Gérer les Équipes (Actions du Coach)

### Test 4.1 : Sélectionner des joueurs pour une équipe

1. Dans la section équipe, dans le champ **"ID Équipe pour Actions"**, entrez : `LAL`
2. Cliquez sur **"Sélectionner Joueurs"**
3. Une fenêtre popup apparaît : `Entrez les IDs des joueurs (séparés par des virgules):`
4. Entrez : `1,2` (les IDs des joueurs de l'équipe LAL)
5. Cliquez sur **OK**
6. ✅ **Vérification** :
   - Message vert : `✅ Joueurs sélectionnés: ...`
   - Les joueurs sont maintenant associés à l'équipe

### Test 4.2 : Changer la stratégie d'une équipe

1. Assurez-vous que l'ID d'équipe est toujours `LAL` dans le champ
2. Cliquez sur **"Changer Stratégie"**
3. Une fenêtre popup apparaît : `Entrez la stratégie:`
4. Entrez : `FAST_BREAK` (ou `DEFENSIVE`, `OFFENSIVE`)
5. Cliquez sur **OK**
6. ✅ **Vérification** :
   - Message vert : `✅ Stratégie changée: ...`

### Test 4.3 : Voir les joueurs d'une équipe

1. Assurez-vous que l'ID d'équipe est `LAL`
2. Cliquez sur **"Voir Joueurs"**
3. ✅ **Vérification** :
   - Une section s'affiche : "Joueurs de l'équipe"
   - Vous voyez la liste des IDs des joueurs de l'équipe (1, 2)

---

## 🏆 ÉTAPE 5 : Créer un Match

### Test 5.1 : Créer un match

1. Dans la section **"🎮 Créer un Match"** (carte de droite)
2. Remplissez le formulaire :
   - **ID Match** : `game1`
   - **Équipe Domicile** : `LAL`
   - **Équipe Visiteur** : `BOS`
3. Cliquez sur **"Créer Match"**
4. ✅ **Vérification** :
   - Message vert : `✅ Match créé: game1`
   - Dans le **Tableau de Score** (en bas), les noms des équipes apparaissent :
     - HOME : `LAL`
     - AWAY : `BOS`
   - Le statut est : `SCHEDULED` (orange)

### Test 5.2 : Test d'erreur - Équipe inexistante

1. Essayez de créer un match avec une équipe qui n'existe pas :
   - ID Match : `game2`
   - Équipe Domicile : `XXX` (n'existe pas)
   - Équipe Visiteur : `LAL`
2. Cliquez sur **"Créer Match"**
3. ✅ **Vérification** :
   - Message d'erreur rouge : `❌ Erreur: Les équipes suivantes n'existent pas : XXX (domicile)`
   - Une alerte popup peut aussi apparaître avec les détails
   - Le match n'est pas créé

---

## ⚽ ÉTAPE 6 : Démarrer et Jouer un Match

### Test 6.1 : Démarrer le match

1. Assurez-vous que le champ **"ID Match pour Actions"** contient : `game1`
   - (Il devrait être rempli automatiquement après la création)
2. Cliquez sur **"Démarrer Match"**
3. ✅ **Vérification** :
   - Message vert : `✅ Match démarré: ...`
   - Dans le **Tableau de Score**, le statut change :
     - De `SCHEDULED` (orange) à `IN_PROGRESS` (vert)
   - Le score commence à se rafraîchir automatiquement

### Test 6.2 : Enregistrer des actions - Panier HOME

1. Dans le **Tableau de Score**, section "Actions Rapides - HOME"
2. Cliquez sur **"HOME 2pts"**
3. ✅ **Vérification** :
   - Message vert dans les logs : `✅ Action enregistrée: SCORE (HOME) pour joueur 1`
   - Le score HOME augmente de 2 points (0 → 2)
   - Le score se met à jour automatiquement

### Test 6.3 : Enregistrer plus d'actions

Enregistrez plusieurs actions pour créer un vrai match :

1. **HOME marque 2 points** : Cliquez sur "HOME 2pts"
   - Score HOME : 2, AWAY : 0

2. **AWAY marque 3 points** : Cliquez sur "AWAY 3pts"
   - Score HOME : 2, AWAY : 3

3. **HOME marque 2 points** : Cliquez sur "HOME 2pts"
   - Score HOME : 4, AWAY : 3

4. **HOME rebond** : Cliquez sur "HOME Rebond"
   - Le score ne change pas (rebond ne compte pas de points)

5. **AWAY marque 2 points** : Cliquez sur "AWAY 2pts"
   - Score HOME : 4, AWAY : 5

6. **HOME passe décisive** : Cliquez sur "HOME Passe D"
   - Le score ne change pas

✅ **Vérification** :
- Le score se met à jour en temps réel après chaque action
- Les informations du match s'affichent :
  - Quart-temps : 1
  - Temps restant : se décrémente
  - Statut : IN_PROGRESS (vert)

### Test 6.4 : Utiliser un joueur spécifique pour les actions

1. Dans le **Tableau de Score**, section "ID Joueur pour Actions"
2. Entrez un ID de joueur : `2` (pour utiliser le joueur 2 au lieu du joueur 1)
3. Enregistrez une action (ex: "HOME 2pts")
4. ✅ **Vérification** :
   - Le message indique : `✅ Action enregistrée: SCORE (HOME) pour joueur 2`
   - Les stats du joueur 2 sont mises à jour (si vous les affichez)

---

## 📊 ÉTAPE 7 : Voir le Score et les Statistiques

### Test 7.1 : Voir le score manuellement

1. Cliquez sur **"Voir Score"** dans la section Match
2. ✅ **Vérification** :
   - Le score est affiché dans le Tableau de Score
   - Les informations sont à jour

### Test 7.2 : Vérifier les stats d'un joueur pendant le match

1. Dans la section Joueur, chargez les stats du joueur `1`
2. Activez l'**Auto-rafraîchissement** si ce n'est pas déjà fait
3. Enregistrez quelques actions avec ce joueur (via les boutons d'actions)
4. ✅ **Vérification** :
   - Les stats du joueur se mettent à jour automatiquement
   - Vous voyez les points, rebonds, passes augmenter en temps réel

---

## 🎯 ÉTAPE 8 : Mettre à Jour les Statistiques Manuellement

### Test 8.1 : Mettre à jour les stats d'un joueur

1. Dans la section Match, section **"📊 Modifier Stats Joueur"**
2. Remplissez :
   - **ID Joueur** : `1`
   - **Points** : `10`
   - **Rebonds** : `5`
   - **Passes Décisives** : `3`
3. Cliquez sur **"Mettre à jour Stats"**
4. ✅ **Vérification** :
   - Message vert : `✅ Stats mises à jour pour joueur 1`
   - Si vous affichez les stats du joueur 1, elles sont mises à jour

### Test 8.2 : Vérifier la mise à jour

1. Dans la section Joueur, chargez les stats du joueur `1`
2. ✅ **Vérification** :
   - Points : 10 (ou plus si des actions ont été enregistrées)
   - Rebonds : 5 (ou plus)
   - Passes Décisives : 3 (ou plus)

---

## 🏁 ÉTAPE 9 : Terminer un Match

### Test 9.1 : Terminer le match

1. Dans la section Match, cliquez sur **"Terminer Match"**
2. Une fenêtre de confirmation apparaît : `Êtes-vous sûr de vouloir terminer le match ?`
3. Cliquez sur **OK** (ou Annuler pour continuer)
4. ✅ **Vérification** :
   - Message vert : `✅ Match terminé: ...`
   - Dans le Tableau de Score, le statut change :
     - De `IN_PROGRESS` (vert) à `FINISHED` (rouge)

### Test 9.2 : Vérifier les records des équipes

1. Dans la section Équipe, cliquez sur **"🔄 Rafraîchir"** pour voir les équipes
2. ✅ **Vérification** :
   - L'équipe gagnante a maintenant **1 victoire**
   - L'équipe perdante a maintenant **1 défaite**
   - Les statistiques sont mises à jour

---

## 🔄 ÉTAPE 10 : Scénario Complet (Test d'Intégration)

### Test 10.1 : Match complet de A à Z

Suivez cet ordre pour tester un scénario complet :

1. ✅ **Créer 4 joueurs** :
   - Joueur 1 : LeBron James (LAL)
   - Joueur 2 : Anthony Davis (LAL)
   - Joueur 3 : Jayson Tatum (BOS)
   - Joueur 4 : Jaylen Brown (BOS)

2. ✅ **Créer 2 équipes** :
   - LAL (Lakers)
   - BOS (Celtics)

3. ✅ **Sélectionner les joueurs pour chaque équipe** :
   - LAL : joueurs 1, 2
   - BOS : joueurs 3, 4

4. ✅ **Créer un match** :
   - game1 : LAL vs BOS

5. ✅ **Démarrer le match**

6. ✅ **Enregistrer au moins 10 actions** :
   - Mix de paniers HOME et AWAY
   - Quelques rebonds et passes
   - Utilisez différents joueurs

7. ✅ **Vérifier le score final** dans le Tableau de Score

8. ✅ **Vérifier les stats de chaque joueur** :
   - Chargez les stats de chaque joueur (1, 2, 3, 4)
   - Vérifiez que leurs statistiques ont été mises à jour

9. ✅ **Terminer le match**

10. ✅ **Vérifier les records des équipes** :
    - Rafraîchissez la liste des équipes
    - Vérifiez les victoires/défaites

---

## 🐛 ÉTAPE 11 : Tests de Gestion d'Erreurs

### Test 11.1 : Joueur inexistant

1. Dans "Voir les Statistiques", entrez un ID qui n'existe pas : `999`
2. Cliquez sur **"Charger les Stats"**
3. ✅ **Vérification** :
   - Alerte : `Joueur non trouvé`
   - Pas de stats affichées

### Test 11.2 : Équipe inexistante pour actions

1. Dans la section Équipe, entrez un ID d'équipe inexistant : `XXX`
2. Cliquez sur **"Sélectionner Joueurs"**
3. ✅ **Vérification** :
   - Message d'erreur : `❌ Erreur 404: ...`
   - Alerte : `Coach non trouvé. Assurez-vous d'avoir créé l'équipe d'abord...`

### Test 11.3 : Match inexistant

1. Dans la section Match, changez l'ID du match pour actions : `game-inexistant`
2. Cliquez sur **"Démarrer Match"**
3. ✅ **Vérification** :
   - Message d'erreur : `❌ Erreur: ...`

---

## ✅ Checklist de Tests Frontend

Cochez chaque test au fur et à mesure :

### Tests de Base
- [ ] Créer un joueur
- [ ] Créer plusieurs joueurs (3-4)
- [ ] Voir les stats d'un joueur
- [ ] Activer l'auto-rafraîchissement des stats
- [ ] Créer une équipe
- [ ] Créer plusieurs équipes (2)
- [ ] Voir toutes les équipes créées
- [ ] Sélectionner des joueurs pour une équipe
- [ ] Changer la stratégie d'une équipe
- [ ] Voir les joueurs d'une équipe

### Tests de Match
- [ ] Créer un match
- [ ] Démarrer un match
- [ ] Enregistrer une action (HOME 2pts)
- [ ] Enregistrer plusieurs actions (HOME et AWAY)
- [ ] Voir le score se mettre à jour automatiquement
- [ ] Utiliser un joueur spécifique pour les actions
- [ ] Mettre à jour les stats d'un joueur manuellement
- [ ] Terminer un match
- [ ] Vérifier les records des équipes après le match

### Tests d'Erreurs
- [ ] Tenter de créer une équipe qui existe déjà
- [ ] Tenter de créer un match avec une équipe inexistante
- [ ] Tenter de voir les stats d'un joueur inexistant
- [ ] Tenter d'utiliser une équipe inexistante pour les actions

### Tests d'Intégration
- [ ] Scénario complet : Créer joueurs → Équipes → Match → Actions → Terminer
- [ ] Vérifier que les stats se mettent à jour en temps réel
- [ ] Vérifier que le score se met à jour automatiquement
- [ ] Vérifier la communication inter-services (création auto d'équipe)

---

## 💡 Conseils pour les Tests

1. **Commencez simple** : Testez d'abord la création de joueurs et d'équipes
2. **Observez les logs** : Les messages verts/rouges vous indiquent si tout fonctionne
3. **Testez les erreurs** : Vérifiez que les validations fonctionnent
4. **Utilisez l'auto-rafraîchissement** : Pour voir les stats se mettre à jour en temps réel
5. **Faites un match complet** : Pour tester toute la chaîne de fonctionnalités

---

## 🎯 Tests Prioritaires (Minimum)

Si vous avez peu de temps, testez au minimum :

1. ✅ Créer 2 joueurs
2. ✅ Créer 2 équipes
3. ✅ Créer un match
4. ✅ Démarrer le match
5. ✅ Enregistrer 3-4 actions
6. ✅ Vérifier le score
7. ✅ Vérifier les stats d'un joueur
8. ✅ Terminer le match

Ces 8 tests couvrent les fonctionnalités principales !

---

**Bon test ! 🏀**

