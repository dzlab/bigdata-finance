rem Start script for the CATALINA Server

if exist "CATALINA_HOME" goto deploy
echo Cannot find "CATALINA_HOME"
set "CATALINA_HOME=C:\bin\apache-tomcat-8.0.15"

:deploy
set "EXECUTABLE=%CATALINA_HOME%\bin\catalina.bat"

rem mvn -U -DskipTests clean package

echo "Copying war file to %CATALINA_HOME%\webapps\visualization"
rmdir /S /Q %CATALINA_HOME%\webapps\visualization
copy /Y target\visualization.war %CATALINA_HOME%\webapps\

echo "Starting catalina server" 
call "%EXECUTABLE%" start