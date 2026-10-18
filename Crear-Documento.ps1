$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$destino = Join-Path $PSScriptRoot 'evidencias\Documento-pruebas-AA5-EV02.docx'
if (Test-Path -LiteralPath $destino) { throw 'El documento ya existe. Conserva tus cambios; renómbralo antes de regenerarlo.' }
$coleccion = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'postman\Codivis-AA5-EV02.postman_collection.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$resultado = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'evidencias\ejecucion-postman-cli.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$registro = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'evidencias\resultado-postman-cli.txt') -Raw
$estadistica = [regex]::Match($registro, '\|\s*assertions\s*\|\s*(\d+)\s*\|\s*(\d+)')
if ($resultado.codigoSalida -ne 0 -or -not $estadistica.Success) { throw 'Se necesita un resultado CLI válido antes de elaborar el informe.' }
$parrafos = [Collections.Generic.List[string]]::new()
function P([string]$texto, [bool]$titulo = $false) {
    $escapado = [Security.SecurityElement]::Escape($texto)
    $formato = if ($titulo) { '<w:rPr><w:b/><w:sz w:val="28"/></w:rPr>' } else { '' }
    $parrafos.Add("<w:p><w:r>$formato<w:t xml:space=`"preserve`">$escapado</w:t></w:r></w:p>")
}
P 'INFORME DE PRUEBAS DE API — CODIVIS' $true
P 'Tatiana Guerrero'
P 'SENA — Análisis y Desarrollo de Software'
P 'Evidencia GA7-220501096-AA5-EV02. API.'
P "Fecha de comprobación técnica: $($resultado.fecha)"
P 'Estado: comprobación con Postman CLI realizada; pendiente incorporar capturas, resultados y video de Postman Desktop.'
P 'Introducción' $true
P 'Este informe documenta las pruebas del servicio de registro e inicio de sesión desarrollado en AA5-EV01. Se verifica el contrato HTTP y JSON con solicitudes positivas y negativas. Se incluyen el código del servicio, la colección reproducible y el registro de ejecución técnica.'
P 'Objetivo' $true
P 'Comprobar que el registro valida los datos, impide usuarios duplicados y que la autenticación acepta credenciales correctas y rechaza las incorrectas. Verificar también los métodos HTTP y formatos admitidos.'
P 'Alcance y entorno' $true
P 'Servicio Java/Spring Boot con persistencia local, formularios application/x-www-form-urlencoded y respuestas JSON. Base URL: http://localhost:18086/crm/api. El login no emite tokens ni crea sesiones. Datos de demostración, sin cuentas personales.'
P 'Herramientas: Postman CLI 1.75.0 para la comprobación técnica. Postman Desktop instalado: 12.32.4. Registrar la versión y fecha reales de la ejecución visual al completar las capturas.'
P 'Procedimiento' $true
P 'Iniciar el servicio, importar la colección AA5-EV02 en Postman y ejecutar las 17 solicitudes en orden con una iteración. La primera solicitud prepara usuarios únicos. Comparar estado HTTP, código de negocio, indicador ok, mensaje y ausencia de contraseñas o hashes. Guardar capturas de solicitudes, respuestas, pruebas y resumen; grabar el video.'
P 'Resumen de comprobación con Postman CLI' $true
P "Objetivo real: $($resultado.baseUrl). Código de salida: $($resultado.codigoSalida). Aserciones ejecutadas: $($estadistica.Groups[1].Value); fallidas: $($estadistica.Groups[2].Value). Consultar resultado-postman-cli.txt y ejecucion-postman-cli.json. Estos resultados no se atribuyen a Postman Desktop."
P 'Casos de prueba y espacios para evidencia visual' $true
foreach ($caso in $coleccion.item) {
    $tests = $caso.event[0].script.exec -join "`n"
    $esperado = [regex]::Match($tests, 'to.have.status\((\d+)\)').Groups[1].Value
    $codigo = [regex]::Match($tests, 'data.codigo\).to.eql\("([A-Z_]+)"\)').Groups[1].Value
    $bloque = [regex]::Match($registro, '(?s)Root ' + [regex]::Escape($caso.name) + '\r?\n(.*?)(?=\r?\nRoot |\r?\n-{10}|$)').Groups[1].Value
    $real = [regex]::Match($bloque, '\[(\d{3}) ').Groups[1].Value
    $aprobadas = [regex]::Matches($bloque, '\bPass\s').Count
    P $caso.name $true
    P ("Solicitud: {0} {1}" -f $caso.request.method, $caso.request.url.Replace('{{baseUrl}}', $resultado.baseUrl))
    $datos = if ($caso.request.body.mode -eq 'urlencoded') { ($caso.request.body.urlencoded | ForEach-Object { "$($_.key)=$($_.value)" }) -join '&' } elseif ($caso.request.body.mode -eq 'raw') { $caso.request.body.raw } else { 'Sin cuerpo.' }
    P "Datos: $datos"
    P "Resultado esperado: HTTP $esperado, código $codigo."
    P "Resultado observado en Postman CLI: HTTP $real; $aprobadas de 2 aserciones aprobadas."
    P 'Resultado observado en Postman Desktop: PENDIENTE de registrar tras la ejecución visual.'
    P 'CAPTURA: insertar aquí la solicitud y su respuesta real en Postman, con el estado HTTP y Test Results.'
}
P 'Resumen visual de Postman Desktop' $true
P 'PENDIENTE: insertar captura de Run collection. Anotar fecha, solicitudes ejecutadas, aserciones aprobadas/fallidas y coincidencia con lo esperado.'
P 'Conclusiones' $true
P 'La ejecución técnica con Postman CLI verificó los escenarios de la colección sobre el servicio compilado. Las respuestas esperadas de error forman parte de las validaciones; recibir un 400 o 401 en esos casos no significa que la prueba haya fallado.'
P 'PENDIENTE: completar la conclusión con los resultados observados en Postman Desktop y adjuntar las capturas y el video antes de entregar.'
P 'Referencias' $true
P 'Guía de aprendizaje 7, apartado 3.1.5, evidencia GA7-220501096-AA5-EV02. Contrato: ENDPOINT.txt. Repositorio: https://github.com/taticodivis/Tatiana_guerrero_AA5_EV02.'
$documento = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>' + ($parrafos -join '') + '<w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1134"/></w:sectPr></w:body></w:document>'
$archivos = @{
    '[Content_Types].xml' = '<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/></Types>'
    '_rels/.rels' = '<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>'
    'word/document.xml' = $documento
}
$zip = [IO.Compression.ZipFile]::Open($destino, [IO.Compression.ZipArchiveMode]::Create)
try { foreach ($nombre in $archivos.Keys) { $writer = [IO.StreamWriter]::new($zip.CreateEntry($nombre).Open(), [Text.UTF8Encoding]::new($false)); try { $writer.Write($archivos[$nombre]) } finally { $writer.Dispose() } } } finally { $zip.Dispose() }
Write-Output $destino
