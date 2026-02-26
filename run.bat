@echo off
echo Starting Bank Management System...
java -jar BankManagementSystem.jar
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Failed to start application!
    echo Please make sure Java is installed.
    echo.
    pause
)
