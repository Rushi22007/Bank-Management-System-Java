@echo off
REM WhatsApp Report Sender for Bank Management System
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║     BANK MANAGEMENT SYSTEM - WHATSAPP REPORT SENDER               ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

REM Check if whatsapp.properties is configured
findstr /C:"YOUR_ACCOUNT_SID_HERE" whatsapp.properties >nul
if %errorlevel% equ 0 (
    echo WARNING: WhatsApp not configured!
    echo Please edit whatsapp.properties file before running this program.
    echo.
    echo Setup Instructions:
    echo 1. Create a Twilio account at https://www.twilio.com/
    echo 2. Get your Account SID and Auth Token
    echo 3. Set up WhatsApp Sandbox for testing
    echo 4. Update whatsapp.properties with your credentials
    echo.
    pause
    exit /b 1
)

findstr /C:"YOUR_AUTH_TOKEN_HERE" whatsapp.properties >nul
if %errorlevel% equ 0 (
    echo WARNING: WhatsApp Auth Token not configured!
    echo Please set twilio.auth.token in whatsapp.properties.
    echo.
    pause
    exit /b 1
)

REM Run the WhatsAppReportSender
java -cp "lib/*;out/production/Bank Management System" bank.management.system.WhatsAppReportSender

echo.
if /I not "%~1"=="--no-pause" pause
