#!/bin/bash

echo "========================================"
echo "  NBA Actor Framework - Démarrage Rapide"
echo "========================================"
echo ""

echo "[1/6] Compilation du projet..."
mvn clean install -q
if [ $? -ne 0 ]; then
    echo "ERREUR: La compilation a échoué!"
    exit 1
fi
echo "[OK] Compilation réussie!"
echo ""

echo "[2/6] Démarrage d'Eureka Server..."
cd eureka-server
mvn spring-boot:run > ../logs/eureka.log 2>&1 &
EUREKA_PID=$!
cd ..
sleep 15
echo "[OK] Eureka Server démarré (port 8761)"
echo ""

echo "[3/6] Démarrage de Player Service..."
cd nba-player-service
mvn spring-boot:run > ../logs/player.log 2>&1 &
PLAYER_PID=$!
cd ..
sleep 10
echo "[OK] Player Service démarré (port 8081)"
echo ""

echo "[4/6] Démarrage de Team Service..."
cd nba-team-service
mvn spring-boot:run > ../logs/team.log 2>&1 &
TEAM_PID=$!
cd ..
sleep 10
echo "[OK] Team Service démarré (port 8082)"
echo ""

echo "[5/6] Démarrage de Game Service..."
cd nba-game-service
mvn spring-boot:run > ../logs/game.log 2>&1 &
GAME_PID=$!
cd ..
sleep 10
echo "[OK] Game Service démarré (port 8083)"
echo ""

echo "[6/6] Démarrage du serveur web pour l'interface..."
cd nba-frontend
python3 -m http.server 8000 > ../logs/web.log 2>&1 &
WEB_PID=$!
cd ..
sleep 3
echo "[OK] Serveur web démarré (port 8000)"
echo ""

echo "========================================"
echo "  Tous les services sont démarrés!"
echo "========================================"
echo ""
echo "URLs disponibles:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - Interface Web: http://localhost:8000"
echo "  - Player API: http://localhost:8081"
echo "  - Team API: http://localhost:8082"
echo "  - Game API: http://localhost:8083"
echo ""
echo "PIDs des processus:"
echo "  - Eureka: $EUREKA_PID"
echo "  - Player: $PLAYER_PID"
echo "  - Team: $TEAM_PID"
echo "  - Game: $GAME_PID"
echo "  - Web: $WEB_PID"
echo ""

# Créer un script pour arrêter tous les services
cat > stop-all.sh << EOF
#!/bin/bash
echo "Arrêt de tous les services..."
kill $EUREKA_PID $PLAYER_PID $TEAM_PID $GAME_PID $WEB_PID 2>/dev/null
echo "Services arrêtés!"
EOF
chmod +x stop-all.sh

echo "Pour arrêter tous les services, exécutez: ./stop-all.sh"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter (ou laissez tourner en arrière-plan)"
echo ""

# Ouvrir les navigateurs
if command -v xdg-open &> /dev/null; then
    xdg-open http://localhost:8000
    xdg-open http://localhost:8761
elif command -v open &> /dev/null; then
    open http://localhost:8000
    open http://localhost:8761
fi

# Attendre l'interruption
trap "echo ''; echo 'Arrêt des services...'; kill $EUREKA_PID $PLAYER_PID $TEAM_PID $GAME_PID $WEB_PID 2>/dev/null; exit" INT TERM
wait

