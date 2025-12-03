@echo off
REM Encryptify Launcher for Windows

set DIR=%~dp0
java --module-path "%DIR%lib" --add-modules javafx.controls -jar "%DIR%encryptify.jar" %*
