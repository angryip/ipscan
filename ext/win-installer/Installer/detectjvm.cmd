:: @echo off
set JAVA_BIN="%JAVA_HOME%\bin\java.exe"

if not exist %JAVA_BIN% set JAVA_BIN="java.exe"

%JAVA_BIN% -version 2>&1 | findstr /I "64-bit"
if %errorlevel% == 0 exit /b 64

%JAVA_BIN% -version 2>&1 | findstr /I "32-bit"
if %errorlevel% == 0 exit /b 32
