@echo off
setlocal enabledelayedexpansion
where gradle >nul 2>nul
if %errorlevel%==0 (
  gradle %*
  exit /b %errorlevel%
)
set VER=8.10.2
if "%GRADLE_USER_HOME%"=="" (set BASE=%USERPROFILE%\.gradle\bootstrap\gradle-%VER%) else (set BASE=%GRADLE_USER_HOME%\bootstrap\gradle-%VER%)
set BIN=%BASE%\gradle-%VER%\bin\gradle.bat
if not exist "%BIN%" (
  if not exist "%BASE%" mkdir "%BASE%"
  set ZIP=%BASE%\gradle.zip
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing 'https://services.gradle.org/distributions/gradle-%VER%-bin.zip' -OutFile '%ZIP%'"
  if errorlevel 1 exit /b 1
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ZIP%' '%BASE%'"
  if errorlevel 1 exit /b 1
)
call "%BIN%" %*
exit /b %errorlevel%
