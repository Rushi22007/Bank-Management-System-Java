@echo off
echo ========================================
echo Building Bank Management System JAR
echo ========================================

REM Compile Java files
echo.
echo [1/4] Compiling Java source files...
javac -encoding UTF-8 -cp "lib\*;src" -d out src\bank\management\system\*.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)
echo ✓ Compilation successful!

REM Copy icon resources to out directory
echo.
echo [2/4] Copying resources...
if not exist "out\icon" mkdir "out\icon"
xcopy /Y /Q src\icon\*.png out\icon\ >nul
echo ✓ Resources copied!

REM Create JAR file
echo.
echo [3/4] Creating JAR file...
cd out
jar cfm ..\BankManagementSystem.jar ..\MANIFEST.MF bank\management\system\*.class bank\management\system\*.class icon\*.png
cd ..
echo ✓ JAR file created!

REM Copy libraries to dist folder
echo.
echo [4/4] Creating distribution package...
if not exist "dist" mkdir "dist"
if not exist "dist\lib" mkdir "dist\lib"
copy /Y BankManagementSystem.jar dist\ >nul
xcopy /Y /Q lib\*.jar dist\lib\ >nul
if not exist "dist\db" mkdir "dist\db"
if exist "db\bank.db" copy /Y db\bank.db dist\db\ >nul
echo ✓ Distribution package created!

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo Output files:
echo   - BankManagementSystem.jar (Main application)
echo   - dist\ folder (Complete package)
echo.
echo To run the application:
echo   java -jar BankManagementSystem.jar
echo.
pause
