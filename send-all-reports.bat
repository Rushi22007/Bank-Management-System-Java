@echo off
REM Combined Email and WhatsApp Report Sender
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║   BANK MANAGEMENT SYSTEM - SEND REPORTS (EMAIL + WHATSAPP)        ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.
echo This will send database reports via:
echo   ✓ Email (with PDF attachment)
echo   ✓ WhatsApp (text summary)
echo.

cd /d "%~dp0"

REM Send via Email
echo [1/2] Sending Email Report...
echo ═══════════════════════════════════════════════════════════════════
call send-database-email.bat

echo.
echo.
REM Send via WhatsApp
echo [2/2] Sending WhatsApp Report...
echo ═══════════════════════════════════════════════════════════════════
call send-whatsapp-report.bat --no-pause

echo.
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║              ALL REPORTS SENT SUCCESSFULLY!                        ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.
pause
