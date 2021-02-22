@echo off

REM
set JDKBIN="C:\prg\jdk8\bin"
set PROJECT_DIR="C:\prg\myapps\JChat"

REM Compiling
%JDKBIN%\javac %PROJECT_DIR%\src\ServerLog.java -cp %PROJECT_DIR%\build\classes -d %PROJECT_DIR%\build\classes

REM Running
cd %PROJECT_DIR%\build\classes\
%JDKBIN%\java ServerLog

pause