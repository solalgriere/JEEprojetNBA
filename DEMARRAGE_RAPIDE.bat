@echo off
echo ========================================
echo   NBA Actor Framework - Demarrage Rapide
echo ========================================
echo.

echo [1/6] Compilation du projet...
call mvn clean install -q
if %errorlevel% neq 0 (
    echo ERREUR: La compilation a echoue!
    pause
    exit /b 1
)
echo [OK] Compilation reussie!
echo.

echo [2/6] Demarrage d'Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
timeout /t 15 /nobreak >nul
echo [OK] Eureka Server demarre (port 8761)
echo.

echo [3/6] Demarrage de Player Service...
start "Player Service" cmd /k "cd nba-player-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo [OK] Player Service demarre (port 8081)
echo.

echo [4/6] Demarrage de Team Service...
start "Team Service" cmd /k "cd nba-team-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo [OK] Team Service demarre (port 8082)
echo.

echo [5/6] Demarrage de Game Service...
start "Game Service" cmd /k "cd nba-game-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo [OK] Game Service demarre (port 8083)
echo.

echo [6/6] Demarrage du serveur web pour l'interface...
cd nba-frontend
start "Web Server" cmd /k "python -m http.server 8000"
cd ..
timeout /t 3 /nobreak >nul
echo [OK] Serveur web demarre (port 8000)
echo.

echo ========================================
echo   Tous les services sont demarres!
echo ========================================
echo.
echo URLs disponibles:
echo   - Eureka Dashboard: http://localhost:8761
echo   - Interface Web: http://localhost:8000
echo   - Player API: http://localhost:8081
echo   - Team API: http://localhost:8082
echo   - Game API: http://localhost:8083
echo.
echo Appuyez sur une touche pour ouvrir l'interface web...
pause >nul

start http://localhost:8000
start http://localhost:8761

echo.
echo Pour arreter tous les services, fermez les fenetres de terminal.
pause

