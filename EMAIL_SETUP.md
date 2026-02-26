# Email Database Reports - Setup Guide

## Overview
Your Bank Management System can now send complete database reports via email automatically!

## 📧 Quick Setup (3 steps)

### Step 1: Enable Gmail App Password
Since you're using Gmail, you need to create an **App Password** (regular password won't work):

1. Go to your Google Account: https://myaccount.google.com/
2. Select **Security** from the left menu
3. Enable **2-Step Verification** (if not already enabled)
4. Search for "App passwords" or go to: https://myaccount.google.com/apppasswords
5. Select app: **Mail**
6. Select device: **Windows Computer**
7. Click **Generate**
8. Copy the 16-character password (save it, you'll need it!)

### Step 2: Configure Email Settings
Edit the `email.properties` file in your project root:

```properties
# Replace with your actual Gmail address
mail.sender.email=your-actual-email@gmail.com

# Replace with the App Password you generated (no spaces)
mail.sender.password=abcd efgh ijkl mnop

# Replace with email where you want to receive reports
mail.recipient.email=your-actual-email@gmail.com
```

**Important:** 
- Use the App Password (16 characters), NOT your regular Gmail password
- Remove any spaces from the App Password
- The sender and recipient can be the same email

### Step 3: Send Database Report
Double-click `send-database-email.bat` or run:
```bash
java -cp "lib/*;out/production/Bank Management System" bank.management.system.DatabaseEmailer
```

## 📊 What Gets Emailed?
The complete database report includes:
- ✅ All registered users
- ✅ Additional user details (PAN, Aadhaar, etc.)
- ✅ All active accounts
- ✅ Complete transaction history
- ✅ Current account balances
- ✅ Total system statistics

## 🔧 Files Created
- `EmailService.java` - Handles email sending
- `DatabaseEmailer.java` - Generates and sends database reports
- `email.properties` - Email configuration
- `send-database-email.bat` - Quick run script
- `lib/javax.mail.jar` - JavaMail API library

## 🎯 Usage Examples

### Send database report to configured email:
```bash
send-database-email.bat
```

### Or run via Java command:
```bash
cd "r:\bank mangement system idea\Bank Management System"
java -cp "lib/*;out/production/Bank Management System" bank.management.system.DatabaseEmailer
```

## ❗ Troubleshooting

### "Authentication failed" error
- ✅ Make sure you're using an App Password (not regular password)
- ✅ Verify 2-Step Verification is enabled on Gmail
- ✅ Check email and password in email.properties

### "Email not configured" message
- ✅ Edit email.properties file
- ✅ Replace "your-email@gmail.com" with your actual email
- ✅ Add your App Password

### "Could not connect" error
- ✅ Check your internet connection
- ✅ Verify Gmail SMTP settings (port 587)
- ✅ Make sure firewall isn't blocking the connection

## 🔒 Security Notes
- Keep your App Password secure
- Don't commit email.properties with real credentials to Git
- App Passwords can be revoked anytime from Google Account settings
- Consider using environment variables for production

## 📝 Customization
To customize email content, edit `DatabaseEmailer.java`:
- Modify `generateDatabaseReport()` method
- Change email subject in `email.properties`
- Add custom filters or sections as needed

---
**Generated:** February 26, 2026  
**For:** Bank Management System
