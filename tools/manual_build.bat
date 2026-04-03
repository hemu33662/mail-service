@echo off
echo Starting Manual Build Patch...

set JAR_PATH=target\mail-service-0.0.1-SNAPSHOT.jar
set BUILD_DIR=manual_build

if not exist "%JAR_PATH%" (
    echo Error: %JAR_PATH% not found. Cannot patch.
    exit /b 1
)

:: 1. Cleanup and Prepare
if exist "%BUILD_DIR%" rmdir /S /Q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

:: 2. Extract JAR
echo Extracting JAR...
cd "%BUILD_DIR%"
jar xf "..\%JAR_PATH%"
cd ..

:: 3. Compile Modified Files
echo Compiling Security Classes...
javac -cp "%BUILD_DIR%\BOOT-INF\classes;%BUILD_DIR%\BOOT-INF\lib\*" ^
    -d "%BUILD_DIR%\BOOT-INF\classes" ^
    src\main\java\com\mailservice\security\ApiKeyAuthFilter.java ^
    src\main\java\com\mailservice\security\SecurityConfig.java ^
    src\main\java\com\mailservice\service\MailService.java ^
    src\main\java\com\mailservice\service\AuditService.java

if %errorlevel% neq 0 (
    echo Compilation Failed!
    exit /b 1
)

:: 3.5 Copy Resources
echo Copying Configuration...
copy /Y src\main\resources\clients.json "%BUILD_DIR%\BOOT-INF\classes\clients.json"

:: 4. Re-Package JAR
echo Re-packaging JAR...
cd "%BUILD_DIR%"
jar c0mf META-INF\MANIFEST.MF "..\%JAR_PATH%" .
cd ..

:: 5. Cleanup
echo Cleaning up...
rmdir /S /Q "%BUILD_DIR%"

echo Build Patch Complete!
