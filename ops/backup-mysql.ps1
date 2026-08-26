param(
    [Parameter(Mandatory = $true)]
    [string]$BackupDirectory,

    [Parameter(Mandatory = $true)]
    [string]$CvDirectory
)

$ErrorActionPreference = "Stop"

$requiredVariables = @("DB_HOST", "DB_PORT", "DB_NAME", "DB_USERNAME", "DB_PASSWORD")
foreach ($variableName in $requiredVariables) {
    if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($variableName))) {
        throw "Required environment variable $variableName is missing."
    }
}

$resolvedCvDirectory = (Resolve-Path -LiteralPath $CvDirectory).Path
$resolvedBackupParent = [System.IO.Path]::GetFullPath($BackupDirectory)
[System.IO.Directory]::CreateDirectory($resolvedBackupParent) | Out-Null

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupRunDirectory = Join-Path $resolvedBackupParent $timestamp
[System.IO.Directory]::CreateDirectory($backupRunDirectory) | Out-Null

$databaseFile = Join-Path $backupRunDirectory "database.sql"
$cvBackupDirectory = Join-Path $backupRunDirectory "cvs"

$env:MYSQL_PWD = $env:DB_PASSWORD
try {
    & mysqldump `
        --host=$env:DB_HOST `
        --port=$env:DB_PORT `
        --user=$env:DB_USERNAME `
        --single-transaction `
        --routines `
        --triggers `
        --databases $env:DB_NAME `
        --result-file=$databaseFile

    if ($LASTEXITCODE -ne 0) {
        throw "mysqldump failed with exit code $LASTEXITCODE."
    }
} finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
}

Copy-Item -LiteralPath $resolvedCvDirectory -Destination $cvBackupDirectory -Recurse

$databaseHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $databaseFile).Hash
Set-Content -LiteralPath (Join-Path $backupRunDirectory "database.sql.sha256") -Value $databaseHash

Write-Output "Backup completed: $backupRunDirectory"
