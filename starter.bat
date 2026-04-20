@echo off

rem Set default profile to lw
set "SPRING_PROFILES_ACTIVE=lw"

rem Check if a profile parameter was provided
if "%~1" neq "" (
    set "SPRING_PROFILES_ACTIVE=%~1"
    echo Profile provided: %SPRING_PROFILES_ACTIVE%
)

echo ==== Starting Secondhand Marketplace Backend ====
echo Using profile: %SPRING_PROFILES_ACTIVE%
echo.

echo 1. Cleaning and compiling project...
call mvn clean compile -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%
if %errorlevel% neq 0 (
    echo Compilation failed. Exiting.
    pause
    exit /b 1
)
echo Compilation successful!
echo.

echo 2. Building and running application...
call mvn package -DskipTests -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%
if %errorlevel% neq 0 (
    echo Package failed. Exiting.
    pause
    exit /b 1
)
java -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% -jar target\secondhand-marketplace-backend-0.0.1-SNAPSHOT.jar
if %errorlevel% neq 0 (
    echo Application failed to start. Exiting.
    pause
    exit /b 1
)

echo ==== Application started successfully! ====
pause