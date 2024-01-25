function DownloadAndInstallGitForWindows
{
    $releaseInfoUrl = "https://api.github.com/repos/git-for-windows/git/releases/latest"

    try
    {
        $releaseInfo = Invoke-RestMethod -Uri $releaseInfoUrl

        $downloadUrl = $releaseInfo.assets.browser_download_url

        $match = $downloadUrl -match 'download/v(?<tag>[\d.]+\.windows\.\d)/Git-(?<version>[\d.]+)-64' -replace '.*v(?<tag>[\d.]+\.windows\.\d)/Git-(?<version>[\d.]+)-64.*', '$1 $2'
        if ($match)
        {
            $matchValues = $match -split ' '
            $matchTag = $matchValues[0]
            $version = $matchValues[1]

            $installerUrl = "https://github.com/git-for-windows/git/releases/download/v$matchTag/Git-$version-64-bit.exe"
            $hashWebPageUrl = "https://github.com/git-for-windows/git/releases/tag/v$matchTag"

            $hashInfo = Invoke-WebRequest -Uri $hashWebPageUrl
            $sha256Match = $hashInfo.Content -match 'Git-' + [regex]::Escape($version) + '-64-bit.exe</td>\s<td>(?<sha256>.*)</td>'

            if ($sha256Match)
            {
                $sha256 = $matches['sha256']

                Write-Output "Installer URL: $installerUrl"
                Write-Output "SHA256 Hash: $sha256"

                $installerPath = "$env:TEMP\GitInstaller.exe"
                Invoke-WebRequest -Uri $installerUrl -OutFile $installerPath

                $computedHash = Get-FileHash -Path $installerPath -Algorithm SHA256 | Select-Object -ExpandProperty Hash
                if ($computedHash -eq $sha256)
                {
                    Start-Process -FilePath $installerPath -ArgumentList "/SUPPRESSMSGBOXES /enable-component windowsterminal" -Wait
                    Remove-Item -Path $installerPath -Force
                    return $true
                }
                else
                {
                    Write-Error "Hash verification failed. Installation aborted."
                }
            }
            else
            {
                Write-Error "Failed to extract SHA256 hash from hash URL."
            }
        }
        else
        {
            Write-Error "Regex match failed. Skipping installation."
        }
    }
    catch
    {
        Write-Error "An error occurred: $_"
    }
    return $false
}

function LinkFunction
{
    $confirmLink = Read-Host "Do you want to create symbolic links? (Y/N)"
    if ($confirmLink -eq 'Y' -or $confirmLink -eq 'y')
    {
        New-Item -ItemType SymbolicLink -Path "$PSScriptRoot\.env" -Target "C:\Lab_Data\configurations-private\AccountLedger\.env" -Confirm
        New-Item -ItemType SymbolicLink -Path "$PSScriptRoot\frequencyOfAccounts.json" -Target "C:\Lab_Data\configurations-private\AccountLedger\frequencyOfAccounts.json" -Confirm
        New-Item -ItemType SymbolicLink -Path "$PSScriptRoot\relationOfAccounts.json" -Target "C:\Lab_Data\configurations-private\AccountLedger\relationOfAccounts.json" -Confirm
        New-Item -ItemType SymbolicLink -Path "$PSScriptRoot\api\http-client.env.json" -Target "C:\Lab_Data\configurations-private\AccountLedger\http-client.env.json" -Confirm
    }
    else
    {
        Write-Host "Symbolic links creation skipped."
    }
}

function CloneFunction
{
    $confirmClone = Read-Host "Do you want to clone the repository? (Y/N)"
    if ($confirmClone -eq 'Y' -or $confirmClone -eq 'y')
    {
        git clone https://github.com/Baneeishaque/configurations-private C:\Lab_Data\configurations-private
        LinkFunction
    }
    else
    {
        Write-Host "Repository cloning skipped."
    }
}

function GitActions
{
    if (Test-Path C:\Lab_Data\configurations-private)
    {
        Set-Location C:\Lab_Data\configurations-private

        if (Test-Path .git)
        {
            $remote = git remote

            if ($remote)
            {
                $confirmPull = Read-Host "Do you want to pull changes from the remote repository? (Y/N)"
                if ($confirmPull -eq 'Y' -or $confirmPull -eq 'y')
                {
                    git pull
                    LinkFunction
                }
                else
                {
                    Write-Host "Git pull skipped."
                }
            }
            else
            {
                git remote add origin https://github.com/Baneeishaque/configurations-private
                git pull -Confirm
                LinkFunction
            }
        }
        else
        {
            $confirmDelete = Read-Host "Do you want to delete the contents of the configurations-private folder? (Y/N)"
            if ($confirmDelete -eq 'Y' -or $confirmDelete -eq 'y')
            {
                Remove-Item -Recurse -Force *
                CloneFunction
            }
            else
            {
                Write-Host "Contents deletion skipped."
            }
        }
    }
    else
    {
        CloneFunction
    }
}

function ProceedWithoutWinget
{
    $confirmDownload = Read-Host "Do you want to download Git for Windows and proceed? (Y/N)"
    if ($confirmDownload -eq 'Y' -or $confirmDownload -eq 'y')
    {
        DownloadAndInstallGitForWindows
        GitActions
    }
    else
    {
        Write-Host "Download and installation of Git for Windows skipped."
    }
}

function ProceedWithoutChocolatey
{
    if (Get-Command "winget" -ErrorAction SilentlyContinue)
    {
        $confirmWingetInstall = Read-Host "Do you want to install Git via winget? (Y/N)"
        if ($confirmWingetInstall -eq 'Y' -or $confirmWingetInstall -eq 'y')
        {
            winget install -e --id = Git.Git -Confirm
            if ($LASTEXITCODE -eq 0)
            {
                GitActions
            }
            else
            {
                ProceedWithoutWinget
            }
        }
        else
        {
            Write-Host "Git installation via winget skipped."
        }
    }
    else
    {
        ProceedWithoutWinget
    }
}

function ProceedWithoutScoop
{
    if (Get-Command "choco" -ErrorAction SilentlyContinue)
    {
        $confirmChocoInstall = Read-Host "Do you want to install Git via Chocolatey? (Y/N)"
        if ($confirmChocoInstall -eq 'Y' -or $confirmChocoInstall -eq 'y')
        {
            choco install -y git -Confirm
            if ($LASTEXITCODE -eq 0)
            {
                GitActions
            }
            else
            {
                ProceedWithoutChocolatey
            }
        }
        else
        {
            Write-Host "Git installation via Chocolatey skipped."
        }
    }
    else
    {
        ProceedWithoutChocolatey
    }
}

function Main
{
    if (Get-Command "git" -ErrorAction SilentlyContinue)
    {
        $confirmGitActions = Read-Host "Do you want to perform Git actions? (Y/N)"
        if ($confirmGitActions -eq 'Y' -or $confirmGitActions -eq 'y')
        {
            GitActions
        }
        else
        {
            Write-Host "Git actions skipped."
        }
    }
    else
    {
        if (Get-Command "scoop" -ErrorAction SilentlyContinue)
        {
            $confirmScoopInstall = Read-Host "Do you want to install Git via Scoop? (Y/N)"
            if ($confirmScoopInstall -eq 'Y' -or $confirmScoopInstall -eq 'y')
            {
                scoop install git -Confirm
                if ($LASTEXITCODE -eq 0)
                {
                    GitActions
                }
                else
                {
                    ProceedWithoutScoop
                }
            }
            else
            {
                Write-Host "Git installation via Scoop skipped."
            }
        }
        else
        {
            ProceedWithoutScoop
        }
    }
}

Main
