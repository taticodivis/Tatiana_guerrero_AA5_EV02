$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $fecha = Get-Date -Format o
    $registro = Join-Path $PSScriptRoot 'evidencias\resultado-postman-cli.txt'
    & postman collection run 'postman/collections/Codivis API - AA5-EV02 - Pruebas de AA5-EV01' --no-report-events 2>&1 | Tee-Object -FilePath $registro
    $codigo = $LASTEXITCODE
    # Mantiene el registro legible en GitHub también al ejecutar desde PowerShell 5.
    $salida = [IO.File]::ReadAllText($registro)
    [IO.File]::WriteAllText($registro, $salida, [Text.UTF8Encoding]::new($false))
    [ordered]@{
        fecha = $fecha
        herramienta = 'Postman CLI'
        baseUrl = 'http://localhost:18086/crm/api'
        codigoSalida = $codigo
        registro = 'resultado-postman-cli.txt'
        evidenciaVisual = 'Pendiente de capturas y video en Postman Desktop'
    } | ConvertTo-Json | Set-Content -LiteralPath 'evidencias\ejecucion-postman-cli.json' -Encoding UTF8
    if ($codigo -ne 0) { throw 'Hay fallos. Revisar resultado-postman-cli.txt antes de documentar.' }
} finally { Pop-Location }
