@echo off
REM Database Email Sender for Bank Management System
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║     BANK MANAGEMENT SYSTEM - DATABASE EMAIL SENDER                ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

REM Check if email.properties is configured
findstr /C:"your-email@gmail.com" email.properties >nul
if %errorlevel% equ 0 (
    echo WARNING: Email not configured!
    echo Please edit email.properties file before running this program.
    echo.
    pause
    exit /b 1
)

REM Run the DatabaseEmailer
java -cp "lib/*;out/production/Bank Management System" bank.management.system.DatabaseEmailer

echo.
pause
