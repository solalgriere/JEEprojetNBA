#!/bin/bash

# Script pour démarrer tous les services
# Usage: ./start-all.sh

echo "Starting Eureka Server..."
cd eureka-server
mvn spring-boot:run &
EUREKA_PID=$!
cd ..

sleep 10

echo "Starting Player Service..."
cd nba-player-service
mvn spring-boot:run &
PLAYER_PID=$!
cd ..

sleep 5

echo "Starting Team Service..."
cd nba-team-service
mvn spring-boot:run &
TEAM_PID=$!
cd ..

sleep 5

echo "Starting Game Service..."
cd nba-game-service
mvn spring-boot:run &
GAME_PID=$!
cd ..

echo "All services started!"
echo "Eureka: http://localhost:8761"
echo "Player Service: http://localhost:8081"
echo "Team Service: http://localhost:8082"
echo "Game Service: http://localhost:8083"
echo ""
echo "Press Ctrl+C to stop all services"

# Attendre l'interruption
trap "kill $EUREKA_PID $PLAYER_PID $TEAM_PID $GAME_PID" EXIT
wait

