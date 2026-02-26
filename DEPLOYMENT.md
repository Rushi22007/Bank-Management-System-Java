# Bank Management System - Deployment Guide

## 📦 Deployment Package Created Successfully!

Your application has been compiled into an executable JAR file ready for deployment.

---

## 🚀 Deployment Options

### Option 1: Run Locally (Easiest)
Simply double-click `run.bat` or run:
```bash
java -jar BankManagementSystem.jar
```

### Option 2: Distribute to Other Computers

**Package to share:**
Share the entire `dist` folder which contains:
- `BankManagementSystem.jar` - Main application
- `lib/` folder - Required libraries (SQLite JDBC, SLF4J)
- `db/` folder - Database files

**Requirements for users:**
- Java Runtime Environment (JRE) 17 or higher

**To run on any computer:**
1. Copy the `dist` folder to target computer
2. Inside the `dist` folder, run:
   ```bash
   java -jar BankManagementSystem.jar
   ```

### Option 3: Create Windows Installer (Advanced)

Use `jpackage` (included with Java 14+) to create a native Windows installer:

```bash
jpackage --input dist ^
  --name "Bank Management System" ^
  --main-jar BankManagementSystem.jar ^
  --main-class bank.management.system.Login ^
  --type exe ^
  --icon src\icon\bank.png ^
  --win-shortcut ^
  --win-menu
```

This creates a `.exe` installer that users can double-click to install.

---

## 🌐 Deploy Online (Web Alternative)

Since this is a desktop application, you cannot deploy it as a website. However, you have options:

### A) Create a GitHub Release
1. Go to your repository: `https://github.com/Rushi22007/Bank-Management-System-Java`
2. Click "Releases" → "Create a new release"
3. Upload `BankManagementSystem.jar` and the `dist` folder as a ZIP
4. Users can download and run

### B) Convert to Web Application (Requires Rewrite)
To deploy online, you would need to:
1. Convert Swing UI to HTML/CSS/JavaScript (React, Angular, etc.)
2. Create REST API backend (Spring Boot, Node.js, etc.)
3. Deploy to cloud platform (AWS, Heroku, Netlify, etc.)

This requires significant rewriting of the application.

---

## 📋 System Requirements

**Development:**
- Java Development Kit (JDK) 17+
- Windows/Linux/Mac

**Users:**
- Java Runtime Environment (JRE) 17+
- 50 MB disk space
- 512 MB RAM minimum

---

## 🔧 Build From Source

To rebuild the JAR file:
```bash
build-jar.bat
```

---

## Demo Credentials

**Card Number:** `1234567890123456`  
**PIN:** `1234`

---

## 📂 File Structure

```
Bank Management System/
├── BankManagementSystem.jar      ← Executable application
├── run.bat                        ← Quick launcher
├── build-jar.bat                  ← Build script
├── dist/                          ← Distribution package
│   ├── BankManagementSystem.jar
│   ├── lib/                       ← Required libraries
│   │   ├── sqlite-jdbc.jar
│   │   ├── slf4j-api.jar
│   │   └── slf4j-simple.jar
│   └── db/                        ← Database files
│       └── bank.db
└── src/                           ← Source code
```

---

## 🐛 Troubleshooting

**"Could not find or load main class"**
- Make sure all JAR files in `lib/` folder are present
- Check that MANIFEST.MF has correct Main-Class entry

**"java is not recognized"**
- Install Java JRE/JDK from https://www.oracle.com/java/technologies/downloads/
- Add Java to system PATH

**Database not found errors**
- Run `DatabaseInitializer` first:
  ```bash
  java -cp "out;lib\*;src" bank.management.system.DatabaseInitializer
  ```

---

## ✅ Next Steps

1. **Test the application:**
   ```bash
   run.bat
   ```

2. **Share with others:**
   - ZIP the `dist` folder
   - Share via email, Google Drive, or GitHub Releases

3. **Create installer (optional):**
   - Use `jpackage` command above for Windows installer

---

## 📱 Alternative: Mobile/Web Version

To make this accessible online or on mobile, consider:
1. Rewriting as a web app (Spring Boot + React/Angular)
2. Using Java frameworks like Vaadin (Java → Web UI)
3. Deploying to cloud platforms (AWS, Azure, Google Cloud)

---

**Repository:** https://github.com/Rushi22007/Bank-Management-System-Java

**Built with:** Java Swing, SQLite, Maven
