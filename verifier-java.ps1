# Script PowerShell pour vérifier les prérequis

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verification des Prerequis" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier Java
Write-Host "Verification de Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host $javaVersion
    Write-Host "✅ Java est installe!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERREUR: Java n'est pas installe ou pas dans le PATH!" -ForegroundColor Red
    Write-Host "Telechargez depuis: https://adoptium.net/" -ForegroundColor Yellow
}
Write-Host ""

# Vérifier Maven
Write-Host "Verification de Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host $mavenVersion
    Write-Host "✅ Maven est installe!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERREUR: Maven n'est pas installe ou pas dans le PATH!" -ForegroundColor Red
    Write-Host "Telechargez depuis: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
}
Write-Host ""

# Vérifier Python (optionnel)
Write-Host "Verification de Python (optionnel)..." -ForegroundColor Yellow
try {
    $pythonVersion = python --version 2>&1
    Write-Host $pythonVersion
    Write-Host "✅ Python est installe!" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Python n'est pas installe (optionnel)" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Appuyez sur une touche pour continuer..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")


