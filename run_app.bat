@echo off
echo Starting Secure Mail Service...

REM Check Java Installation
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH.
    pause
    exit /b 1
)

REM Run Application
if exist "target\mail-service-0.0.1-SNAPSHOT.jar" (
    echo Launching Application...
    java -jar target\mail-service-0.0.1-SNAPSHOT.jar
) else (
    echo Error: target\mail-service-0.0.1-SNAPSHOT.jar not found.
    echo Please run 'mvn clean install' first.
    pause
)
