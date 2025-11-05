# Script para configurar JAVA_HOME en la sesión actual
# NO requiere permisos de administrador
# Usar este script al abrir cada nueva terminal hasta que configures JAVA_HOME permanentemente

$javaPath = "C:\Program Files\Microsoft\jdk-11.0.29.7-hotspot"

Write-Host "Configurando JAVA_HOME para esta sesión..." -ForegroundColor Cyan

# Configurar JAVA_HOME
$env:JAVA_HOME = $javaPath
$env:PATH = "$javaPath\bin;$env:PATH"

# Verificar
Write-Host "✅ JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Green

# Verificar versión
Write-Host ""
Write-Host "Versión de Java:" -ForegroundColor Cyan
java -version

Write-Host ""
Write-Host "✅ Listo! Ahora puedes ejecutar: ./mvnw clean test" -ForegroundColor Green
