# 📱 WhatsApp Integration - Quick Reference

## Bank Management System

---

## 🚀 Quick Start

### 1. Configure WhatsApp (One-time setup)
1. Edit `whatsapp.properties`
2. Add your Twilio credentials
3. Add your WhatsApp number
4. Join Twilio sandbox (send "join [code]" to sandbox number)

### 2. Run Services

#### Send WhatsApp Report
```batch
send-whatsapp-report.bat
```
Sends database summary to WhatsApp

#### WhatsApp Interactive Services  
```batch
whatsapp-services.bat
```
Access balance check, PIN confirmation, transaction alerts

#### Send Both Email & WhatsApp
```batch
send-all-reports.bat
```
Sends reports via both Email and WhatsApp

---

## 📋 Configuration File

File: `whatsapp.properties`

```properties
# Twilio Credentials (Get from console.twilio.com)
twilio.account.sid=ACxxxxxxxxxxxxxxxx
twilio.auth.token=your_auth_token

# WhatsApp Numbers
twilio.whatsapp.from=whatsapp:+14155238886  # Sandbox number
whatsapp.recipient.number=whatsapp:+919876543210  # Your number

# Settings
whatsapp.enabled=true
whatsapp.send.pdf=true
whatsapp.send.summary=true
```

---

## 💬 Message Types

### 1. Report Summary
```
📊 DATABASE REPORT SUMMARY
Generated: 09/03/2026 14:30

KEY METRICS
━━━━━━━━━━━━━━━━━━━━
👥 Users: 5
💳 Accounts: 5
📝 Transactions: 12
💰 Total Balance: ₹1,50,000
```

### 2. Balance Inquiry
```
🏦 BALANCE INQUIRY
═══════════════════════
Account: ****1234
Available Balance: ₹50,000
═══════════════════════
```

### 3. Transaction Alert
```
🏦 TRANSACTION ALERT
═══════════════════════
Account: ****1234
Type: 💰 Deposit
Amount: ₹5,000
Balance: ₹55,000
═══════════════════════
```

### 4. PIN Change
```
🔐 PIN CHANGE CONFIRMATION
═══════════════════════
Your ATM PIN has been changed.
Card: ****-****-****-1234
═══════════════════════
```

---

## 🛠️ Java API Usage

### Send Transaction Alert
```java
WhatsAppInteractive.sendTransactionAlert(pin, "Deposit", 5000);
```

### Send Balance
```java
WhatsAppService service = new WhatsAppService();
service.sendBalanceInquiry(pin, balance);
```

### Send Custom Message
```java
WhatsAppService service = new WhatsAppService();
service.sendMessage("Your custom message");
```

---

## ✅ Features Implemented

- [x] Text message sending
- [x] Report summaries
- [x] Balance inquiry
- [x] Transaction alerts
- [x] PIN change confirmation
- [x] Interactive command interface
- [x] Multiple recipient support
- [x] Error handling
- [x] Configuration validation
- [x] Batch file automation

---

## 📞 Support

For detailed setup instructions, see: **WHATSAPP_SETUP.md**

For Twilio help: https://www.twilio.com/docs/whatsapp
