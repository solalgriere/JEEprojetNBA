@echo off
echo ========================================
echo   Verification de Java
echo ========================================
echo.

echo Verification de Java...
java -version
if %errorlevel% neq 0 (
    echo.
    echo ERREUR: Java n'est pas installe ou pas dans le PATH!
    echo.
    echo Veuillez installer Java 17 ou superieur depuis:
    echo https://adoptium.net/
    echo.
) else (
    echo.
    echo Java est installe!
    echo.
)

echo Verification de Maven...
mvn -version
if %errorlevel% neq 0 (
    echo.
    echo ERREUR: Maven n'est pas installe ou pas dans le PATH!
    echo.
    echo Veuillez installer Maven 3.6+ depuis:
    echo https://maven.apache.org/download.cgi
    echo.
) else (
    echo.
    echo Maven est installe!
    echo.
)

echo Verification de Python (optionnel)...
python --version
if %errorlevel% neq 0 (
    echo Python n'est pas installe (optionnel)
) else (
    echo Python est installe!
)

echo.
echo ========================================
pause


