$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $fecha = Get-Date -Format o
    $registro = Join-Path $PSScriptRoot 'evidencias\resultado-postman-cli.txt'
    & postman collection run 'postman/Codivis-AA5-EV02.postman_collection.json' --no-report-events 2>&1 | Tee-Object -FilePath $registro
    $codigo = $LASTEXITCODE
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
