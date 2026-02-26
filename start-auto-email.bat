@echo off
REM Transaction Monitor - Sends email 1 minute after each transaction
echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║  BANK MANAGEMENT SYSTEM - TRANSACTION MONITOR                     ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.
echo Sender: officialshreeman@gmail.com
echo Recipient: rushikeshchamale5@gmail.com
echo Mode: Email sent 1 minute after each transaction
echo.
echo Starting transaction monitor...
echo Press Ctrl+C to stop
echo.

cd /d "%~dp0"

REM Run the TransactionMonitor
java -cp "lib/*;out/production/Bank Management System" bank.management.system.TransactionMonitor

echo.
pause
