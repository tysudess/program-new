@echo off
setlocal
cd /d "%~dp0\.."
call gradlew.bat clean test packageDistributionForCurrentOS
if errorlevel 1 exit /b %errorlevel%
echo.
echo Build concluido. Verifique build\compose\binaries\
endlocal
