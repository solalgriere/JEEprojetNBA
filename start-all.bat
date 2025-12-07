@echo off
REM Script Windows pour démarrer tous les services

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"

timeout /t 10

echo Starting Player Service...
start "Player Service" cmd /k "cd nba-player-service && mvn spring-boot:run"

timeout /t 5

echo Starting Team Service...
start "Team Service" cmd /k "cd nba-team-service && mvn spring-boot:run"

timeout /t 5

echo Starting Game Service...
start "Game Service" cmd /k "cd nba-game-service && mvn spring-boot:run"

echo All services started!
echo Eureka: http://localhost:8761
echo Player Service: http://localhost:8081
echo Team Service: http://localhost:8082
echo Game Service: http://localhost:8083

pause

