@echo off
echo Patching JAR with new configuration...

if not exist "target\mail-service-0.0.1-SNAPSHOT.jar" (
    echo Error: JAR file not found in target folder.
    pause
    exit /b 1
)

mkdir temp_patch\BOOT-INF\classes 2>nul
copy /Y src\main\resources\clients.json temp_patch\BOOT-INF\classes\clients.json

echo Updating JAR...
jar uf target\mail-service-0.0.1-SNAPSHOT.jar -C temp_patch BOOT-INF/classes/clients.json

echo Cleaning up...
rmdir /S /Q temp_patch

echo Success! Configuration updated in JAR.

