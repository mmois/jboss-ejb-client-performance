@echo off
"C:\Program Files (x86)\Java\jdk1.6.0_21\bin\java.exe" -Xms64M -Xmx512M -XX:MaxPermSize=256M -jar target\client-0.0.1-SNAPSHOT-jar-with-dependencies.jar 100 5 2
pause