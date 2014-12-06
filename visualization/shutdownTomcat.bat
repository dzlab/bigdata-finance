rem Stop script for the CATALINA Server

if exist "CATALINA_HOME" goto stop
echo Cannot find "CATALINA_HOME"
set "CATALINA_HOME=C:\bin\apache-tomcat-8.0.15"

:stop
set "EXECUTABLE=%CATALINA_HOME%\bin\catalina.bat"

rem Stopping catalina server 
call "%EXECUTABLE%" stop