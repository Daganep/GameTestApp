<#
.SYNOPSIS
  Publish workflow: git add -> build check -> commit -> push.

.DESCRIPTION
  Step 1: stage all new, modified and deleted files (git add -A).
  Step 2: run the Gradle build to check for critical errors (default task: :app:assembleDebug).
          If the build fails the script ABORTS before committing.
  Step 3: commit with the provided (or auto-generated) message. The message is passed
          through a UTF-8 temp file (git commit -F) so non-ASCII text survives intact.
  Step 4: push the current branch to origin.

.PARAMETER Message
  Commit message. If omitted, a neutral message is generated from the staged changes.

.PARAMETER WithTests
  Include unit tests (:app:testDebugUnitTest) in the build check.

.PARAMETER Task
  Custom Gradle task to run for the check (overrides the default task).

.PARAMETER SkipCheck
  Skip the build check step (not recommended).

.PARAMETER NoPush
  Stop after the commit; do not push.

.EXAMPLE
  .\publish.ps1 -Message "feat: add snake game logic"

.EXAMPLE
  .\publish.ps1 -Message "fix: collision detection" -WithTests
#>
[CmdletBinding()]
param(
    [string]$Message,
    [switch]$WithTests,
    [string]$Task,
    [switch]$SkipCheck,
    [switch]$NoPush
)

$ErrorActionPreference = 'Stop'

try { [Console]::OutputEncoding = [System.Text.Encoding]::UTF8 } catch { }

function Step($n, $text)  { Write-Host ''; Write-Host ("[{0}/4] {1}" -f $n, $text) -ForegroundColor Cyan }
function Ok($text)        { Write-Host "  + $text" -ForegroundColor Green }
function Fail($text)      { Write-Host "  - $text" -ForegroundColor Red }

# --- Resolve repository root ---------------------------------------------------
$RepoRoot = & git rev-parse --show-toplevel 2>$null
if ($LASTEXITCODE -ne 0 -or -not $RepoRoot) {
    Fail "Not a Git repository."
    exit 1
}
Set-Location $RepoRoot

# --- Step 1: add new and changed files to VCS ----------------------------------
Step 1 "Adding new and changed files to VCS (git add -A)"
& git add -A
$status = @(& git status --porcelain)
if (-not $status -or $status.Count -eq 0) {
    Ok "Nothing to commit - working tree is clean."
    exit 0
}
$added    = @($status | Where-Object { $_ -match '^[ARD]' })
$modified = @($status | Where-Object { $_ -match '^M' })
$deleted  = @($status | Where-Object { $_ -match '^D' })
Ok ("Staged {0} new/renamed, {1} modified, {2} deleted file(s)." -f $added.Count, $modified.Count, $deleted.Count)
& git status --short

# --- Step 2: check for critical errors ------------------------------------------
if ($SkipCheck) {
    Step 2 "Build check skipped (-SkipCheck)"
} else {
    Step 2 "Checking for critical errors (Gradle build)"
    if ($Task) {
        $tasks = @($Task)
    } elseif ($WithTests) {
        $tasks = @(':app:assembleDebug', ':app:testDebugUnitTest')
    } else {
        $tasks = @(':app:assembleDebug')
    }
    Write-Host ("  gradlew " + ($tasks -join ' ')) -ForegroundColor DarkGray
    & .\gradlew.bat @tasks --console=plain
    if ($LASTEXITCODE -ne 0) {
        Fail "BUILD FAILED - critical errors found. Aborting BEFORE commit."
        Fail "Fix the errors above, then re-run this skill."
        exit 1
    }
    Ok "Build succeeded - no critical errors."
}

# --- Step 3: commit --------------------------------------------------------------
Step 3 "Committing"
if (-not $Message) {
    $Message = "chore: {0} added, {1} modified, {2} deleted" -f $added.Count, $modified.Count, $deleted.Count
    Write-Host "  (no -Message given, using auto-generated message)" -ForegroundColor DarkYellow
}
$msgFile = Join-Path $env:TEMP ("commitmsg_{0}.txt" -f [guid]::NewGuid().ToString('N'))
[System.IO.File]::WriteAllText($msgFile, ($Message.Trim() + "`n"), (New-Object System.Text.UTF8Encoding $false))
& git commit -F $msgFile
Remove-Item $msgFile -Force -ErrorAction SilentlyContinue
if ($LASTEXITCODE -ne 0) {
    Fail "git commit failed (check pre-commit hooks / git state)."
    exit 1
}
$commitHash = (& git rev-parse --short HEAD).Trim()
Ok ("Committed {0}" -f $commitHash)

# --- Step 4: push ------------------------------------------------------------------
if ($NoPush) {
    Step 4 "Push skipped (-NoPush)"
} else {
    Step 4 "Pushing to origin"
    $branch = (& git rev-parse --abbrev-ref HEAD).Trim()
    & git push origin $branch
    if ($LASTEXITCODE -ne 0) {
        Fail ("Push failed. Commit {0} is local - run 'git push' again when ready." -f $commitHash)
        exit 1
    }
    Ok ("Pushed {0} to origin" -f $branch)
}

Write-Host ''
Write-Host 'Done.' -ForegroundColor Green
exit 0
