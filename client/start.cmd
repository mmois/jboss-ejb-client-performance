@echo off
java -Xms64M -Xmx2048M -XX:MaxPermSize=256M -jar target\client-0.0.1-SNAPSHOT-jar-with-dependencies.jar 50 5 2
pause