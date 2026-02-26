@echo off
echo Starting Bank Management System...
echo.
java -jar BankManagementSystem.jar
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Failed to start application!
    echo.
    echo Possible reasons:
    echo  - Java is not installed
    echo  - Java version is too old (need Java 17+)
    echo.
    echo Download Java from: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
)
