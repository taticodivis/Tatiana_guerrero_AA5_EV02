$ErrorActionPreference = 'Stop'
& node (Join-Path $PSScriptRoot 'Crear-Informe.js')
if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar el informe HTML.' }
