@echo off
echo Setting up environment variables...
set "CATALINA_HOME=C:\Program Files\tomcat"
set "PATH=%PATH%;C:\Program Files\maven\apache-maven-3.9.9\bin"

echo Killing any running Tomcat process...
taskkill /F /IM tomcat* /T 2>nul
timeout /t 5 /nobreak

echo Building project...
call mvn clean package
if errorlevel 1 (
    echo Maven build failed. Please ensure Maven is installed and in PATH
    echo You can download Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Cleaning old deployment...
if exist "%CATALINA_HOME%\webapps\lendlink.war" (
    del /F /Q "%CATALINA_HOME%\webapps\lendlink.war"
)
if exist "%CATALINA_HOME%\webapps\lendlink" (
    rmdir /S /Q "%CATALINA_HOME%\webapps\lendlink"
)
timeout /t 2 /nobreak

echo Copying new WAR file...
copy "target\lendlink.war" "%CATALINA_HOME%\webapps\lendlink.war"

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

echo Deployment complete! Wait a few seconds for Tomcat to deploy the application.
echo Access the application at: http://localhost:8081/lendlink/
echo.
echo If you see a 404 error, please ensure:
echo 1. Tomcat is running (check http://localhost:8081)
echo 2. The WAR file was copied successfully
echo 3. Check Tomcat logs at: %CATALINA_HOME%\logs\catalina.out
echo.
pause 