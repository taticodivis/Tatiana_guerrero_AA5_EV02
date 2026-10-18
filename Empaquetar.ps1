$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$documento = Join-Path $PSScriptRoot 'evidencias\Documento-pruebas-AA5-EV02.docx'
$video = Join-Path $PSScriptRoot 'evidencias\video\Tatiana_Guerrero_AA5_EV02.mp4'
if (-not (Test-Path -LiteralPath $documento)) { throw 'Falta el documento de pruebas.' }
if (-not (Test-Path -LiteralPath $video) -or (Get-Item -LiteralPath $video).Length -eq 0) { throw 'Falta el video real de Postman en evidencias/video.' }
$revision = [IO.Compression.ZipFile]::OpenRead($documento)
try {
    $reader = [IO.StreamReader]::new($revision.GetEntry('word/document.xml').Open())
    try { $contenido = $reader.ReadToEnd() } finally { $reader.Dispose() }
    if ($contenido -match 'PENDIENTE|CAPTURA: insertar') { throw 'Completa los resultados y sustituye los espacios pendientes del documento antes de entregar.' }
    if (-not ($revision.Entries.FullName -match '^word/media/')) { throw 'El documento todavía no tiene capturas insertadas.' }
} finally { $revision.Dispose() }
$destino = Join-Path $PSScriptRoot 'Tatiana_Guerrero_AA5_EV02.zip'
if (Test-Path -LiteralPath $destino) { throw 'El ZIP ya existe. Renómbralo para conservar la versión anterior antes de empaquetar.' }
$zip = [IO.Compression.ZipFile]::Open($destino, [IO.Compression.ZipArchiveMode]::Create)
try {
    Get-ChildItem -LiteralPath $PSScriptRoot -Recurse -File -Force | Where-Object {
        $_.FullName.Substring($PSScriptRoot.Length + 1) -notmatch '^(\.git|target|data)(\\|$)' -and $_.Extension -notin @('.zip','.rar','.log')
    } | ForEach-Object {
        $nombre = 'Tatiana_Guerrero_AA5_EV02/' + $_.FullName.Substring($PSScriptRoot.Length + 1).Replace('\','/')
        [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, $_.FullName, $nombre, [IO.Compression.CompressionLevel]::Optimal) | Out-Null
    }
} finally { $zip.Dispose() }
Write-Output $destino
