@echo off
echo Stopping Tomcat processes...
taskkill /F /IM java.exe /T 2>nul
timeout /t 3 /nobreak

echo Cleaning Tomcat work directory...
rmdir /S /Q "C:\Program Files\tomcat\work\Catalina" 2>nul

echo Cleaning Tomcat temp directory...
rmdir /S /Q "C:\Program Files\tomcat\temp" 2>nul
mkdir "C:\Program Files\tomcat\temp"

echo Removing old deployment...
rmdir /S /Q "C:\Program Files\tomcat\webapps\lendlink" 2>nul
del /F /Q "C:\Program Files\tomcat\webapps\lendlink.war" 2>nul

echo Cleaning project target directory...
rmdir /S /Q "target" 2>nul

echo Creating fresh directories...
mkdir "C:\Program Files\tomcat\work\Catalina" 2>nul

echo Cleanup complete!
echo.
echo Now you can run deploy.bat to redeploy your application.
pause 