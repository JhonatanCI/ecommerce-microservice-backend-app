# Script para configurar JAVA_HOME permanentemente
# Ejecutar como Administrador: PowerShell -> Clic derecho -> Ejecutar como Administrador

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Configurando JAVA_HOME" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$javaPath = "C:\Program Files\Microsoft\jdk-11.0.29.7-hotspot"

# Verificar que Java existe
if (Test-Path "$javaPath\bin\java.exe") {
    Write-Host "✅ Java encontrado en: $javaPath" -ForegroundColor Green
    
    try {
        # Configurar JAVA_HOME a nivel de sistema (requiere Admin)
        [System.Environment]::SetEnvironmentVariable('JAVA_HOME', $javaPath, 'Machine')
        Write-Host "✅ JAVA_HOME configurado correctamente" -ForegroundColor Green
        
        # Agregar al PATH si no existe
        $machinePath = [System.Environment]::GetEnvironmentVariable('PATH', 'Machine')
        if ($machinePath -notlike "*$javaPath\bin*") {
            $newPath = "$javaPath\bin;$machinePath"
            [System.Environment]::SetEnvironmentVariable('PATH', $newPath, 'Machine')
            Write-Host "✅ Java agregado al PATH del sistema" -ForegroundColor Green
        } else {
            Write-Host "ℹ️  Java ya está en el PATH" -ForegroundColor Yellow
        }
        
        Write-Host ""
        Write-Host "==================================" -ForegroundColor Cyan
        Write-Host "✅ Configuración exitosa!" -ForegroundColor Green
        Write-Host "==================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "IMPORTANTE: Cierra todas las ventanas de PowerShell y abre una nueva para que los cambios surtan efecto." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Luego verifica con:" -ForegroundColor Cyan
        Write-Host "  java -version" -ForegroundColor White
        Write-Host "  echo `$env:JAVA_HOME" -ForegroundColor White
        
    } catch {
        Write-Host "❌ Error: No se pudo configurar JAVA_HOME" -ForegroundColor Red
        Write-Host "Necesitas ejecutar PowerShell como Administrador" -ForegroundColor Red
        Write-Host ""
        Write-Host "Pasos:" -ForegroundColor Yellow
        Write-Host "1. Clic derecho en el ícono de PowerShell" -ForegroundColor White
        Write-Host "2. Seleccionar 'Ejecutar como Administrador'" -ForegroundColor White
        Write-Host "3. Ejecutar: .\setup-java-home.ps1" -ForegroundColor White
    }
    
} else {
    Write-Host "❌ Java no encontrado en: $javaPath" -ForegroundColor Red
    Write-Host "Por favor, instala Java 11 primero" -ForegroundColor Red
}

Write-Host ""
Read-Host "Presiona Enter para salir"
