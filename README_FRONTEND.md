# Interface Web - NBA Actor Framework

## Description

Une interface web simple et moderne pour interagir avec l'application NBA Actor Framework.

## Fonctionnalités

### 🎯 Dashboard Principal

1. **Création de Joueurs**
   - Formulaire pour créer des joueurs NBA
   - Affichage des logs en temps réel

2. **Gestion d'Équipes**
   - Création d'équipes avec coach
   - Sélection de joueurs
   - Ajustement de stratégies

3. **Gestion de Matchs**
   - Création de matchs
   - Démarrage de matchs
   - Visualisation du score en temps réel

4. **Tableau de Score**
   - Affichage du score en temps réel
   - Informations sur le quart-temps et le temps restant
   - Actions rapides (paniers, rebonds, passes)

## Utilisation

### Option 1 : Ouvrir directement dans le navigateur

1. Assurez-vous que tous les services sont démarrés (Eureka, Player, Team, Game)
2. Ouvrez `nba-frontend/index.html` dans votre navigateur
3. L'interface est prête à l'emploi !

### Option 2 : Servir avec un serveur HTTP simple

```bash
# Python 3
cd nba-frontend
python -m http.server 8000

# Node.js (avec http-server)
npx http-server nba-frontend -p 8000

# Puis ouvrir http://localhost:8000
```

## Interface

L'interface comprend :

- **Design moderne** : Gradient violet, cartes élégantes
- **Responsive** : S'adapte à différentes tailles d'écran
- **Logs en temps réel** : Affichage des actions et résultats
- **Tableau de score animé** : Mise à jour automatique du score
- **Actions rapides** : Boutons pour les actions courantes

## Workflow Recommandé

1. **Créer des joueurs** : Créez plusieurs joueurs avec différents IDs
2. **Créer une équipe** : Créez une équipe avec un coach
3. **Sélectionner les joueurs** : Utilisez le bouton "Sélectionner Joueurs"
4. **Créer un match** : Créez un match entre deux équipes
5. **Démarrer le match** : Cliquez sur "Démarrer Match"
6. **Enregistrer des actions** : Utilisez les boutons d'actions rapides
7. **Voir le score** : Le score se met à jour automatiquement

## Notes

- L'interface communique directement avec les APIs REST
- Les logs affichent les succès (vert) et erreurs (rouge)
- Le score se rafraîchit automatiquement toutes les 5 secondes
- En cas d'erreur CORS, servez l'interface via un serveur HTTP

## Compatibilité

- Navigateurs modernes (Chrome, Firefox, Edge, Safari)
- Pas de dépendances externes (HTML/CSS/JS pur)
- Fonctionne hors ligne (une fois chargée)

