@echo off
REM WhatsApp Interactive Services for Bank Management System
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║  BANK MANAGEMENT SYSTEM - WHATSAPP INTERACTIVE SERVICES           ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.
echo Services Available:
echo   - Balance Inquiry
echo   - PIN Change Confirmation
echo   - Transaction Alerts
echo.

cd /d "%~dp0"

REM Check if whatsapp.properties is configured
findstr /C:"YOUR_ACCOUNT_SID_HERE" whatsapp.properties >nul
if %errorlevel% equ 0 (
    echo WARNING: WhatsApp not configured!
    echo Please edit whatsapp.properties file before running this program.
    echo.
    pause
    exit /b 1
)

REM Run the WhatsAppInteractive
java -cp "lib/*;out/production/Bank Management System" bank.management.system.WhatsAppInteractive

echo.
pause
