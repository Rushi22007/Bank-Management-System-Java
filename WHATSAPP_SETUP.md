# WhatsApp Integration Setup Guide
## Bank Management System

This guide will help you set up WhatsApp messaging for your Bank Management System using Twilio.

---

## 📋 Prerequisites

1. **WhatsApp Account** - An active WhatsApp account on your phone
2. **Twilio Account** - Free or paid account at [twilio.com](https://www.twilio.com/)
3. **Phone Number** - Your phone number with WhatsApp installed

---

## 🚀 Quick Setup (Testing with Sandbox)

### Step 1: Create Twilio Account

1. Go to [https://www.twilio.com/try-twilio](https://www.twilio.com/try-twilio)
2. Sign up for a free account
3. Verify your email and phone number

### Step 2: Get Your Credentials

1. Log in to [Twilio Console](https://console.twilio.com/)
2. On the dashboard, you'll see:
   - **Account SID** - Copy this
   - **Auth Token** - Click to reveal and copy

### Step 3: Set Up WhatsApp Sandbox

1. In Twilio Console, go to: **Messaging** → **Try it out** → **Send a WhatsApp message**
   - Or visit: [https://console.twilio.com/us1/develop/sms/try-it-out/whatsapp-learn](https://console.twilio.com/us1/develop/sms/try-it-out/whatsapp-learn)

2. You'll see instructions like:
   ```
   Send "join [your-code]" to +1 415 523 8886
   ```
   Example: `join happy-tiger`

3. **On your phone:**
   - Open WhatsApp
   - Send the message to the number shown
   - You should receive a confirmation message

4. **Copy the sandbox number:**
   - It will be in format: `whatsapp:+14155238886`

### Step 4: Configure the Application

1. Open `whatsapp.properties` file in your project root

2. Update with your credentials:
   ```properties
   # Your Twilio credentials
   twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   twilio.auth.token=your_auth_token_here
   
   # Twilio WhatsApp Sandbox number
   twilio.whatsapp.from=whatsapp:+14155238886
   
   # Your WhatsApp number (with country code)
   whatsapp.recipient.number=whatsapp:+919876543210
   ```

3. **Important:** Replace `+919876543210` with YOUR phone number including country code
   - India: `+91XXXXXXXXXX`
   - USA: `+1XXXXXXXXXX`
   - UK: `+44XXXXXXXXXX`

4. Save the file

### Step 5: Compile the Code

Run the build batch file:
```batch
build-jar.bat
```

Or manually compile:
```batch
javac -encoding UTF-8 -cp "lib/*;out/production/Bank Management System" -d "out/production/Bank Management System" src/bank/management/system/*.java
```

### Step 6: Test WhatsApp Messaging

#### Test 1: Send Report Summary
```batch
send-whatsapp-report.bat
```

You should receive a WhatsApp message with the database summary!

#### Test 2: Interactive Services
```batch
whatsapp-services.bat
```

Try:
- Balance inquiry
- PIN change confirmation
- Transaction alerts

#### Test 3: Send Both Email and WhatsApp
```batch
send-all-reports.bat
```

---

## 🎯 Features Available

### 1. **Report Summary** (Text Message)
- Key metrics (users, accounts, transactions, balance)
- Recent transaction activity
- Top accounts by balance
- Emoji-formatted for easy reading

### 2. **Balance Inquiry**
```
🏦 BALANCE INQUIRY
═══════════════════════
Account: ****1234
Available Balance: ₹50,000
═══════════════════════
```

### 3. **Transaction Alerts**
```
🏦 TRANSACTION ALERT
═══════════════════════
Account: ****1234
Type: 💰 Deposit
Amount: ₹5,000
Balance: ₹55,000
═══════════════════════
```

### 4. **PIN Change Confirmation**
```
🔐 PIN CHANGE CONFIRMATION
═══════════════════════
Your ATM PIN has been changed successfully.
Card: ****-****-****-1234
═══════════════════════
```

---

## 📱 Production Setup (WhatsApp Business API)

For production use with your own number:

### Requirements:
1. **Facebook Business Manager Account**
2. **WhatsApp Business Profile**
3. **Business verification documents**
4. **Approved use case**

### Steps:
1. Apply for WhatsApp Business API access through Twilio
2. Submit business verification documents
3. Wait for approval (can take 1-2 weeks)
4. Get your approved WhatsApp Business number
5. Update `twilio.whatsapp.from` in configuration

### Costs:
- **Sandbox:** FREE for testing
- **Business API:**
  - Conversation-based pricing
  - ~$0.005-0.02 per message (varies by country)
  - Free tier: First 1,000 conversations/month

---

## 🔧 Advanced Configuration

### Send PDF Reports via WhatsApp

WhatsApp requires PDFs to be hosted on a public URL. Options:

#### Option 1: Use Your Website
```java
String pdfUrl = "https://yourwebsite.com/reports/database_report.pdf";
whatsAppService.sendPDF("Here's your report", "reports/database_report.pdf", pdfUrl);
```

#### Option 2: Cloud Storage (Recommended)
- **Google Drive** - Share link with public access
- **Dropbox** - Get public link
- **Amazon S3** - Configure public bucket
- **Cloudinary** - Free media hosting

Example with cloud URL:
```properties
# Add to whatsapp.properties
whatsapp.pdf.url=https://your-cloud-storage.com/reports/database_report.pdf
```

### Automatic Transaction Alerts

Integrate into existing transaction code:

In `Deposit.java` or `Withdrawl.java`, add after successful transaction:
```java
// Send WhatsApp alert
WhatsAppInteractive.sendTransactionAlert(pin, type, amount);
```

---

## 🛠️ Troubleshooting

### Issue: "WhatsApp not configured"
**Solution:** Make sure you've updated `whatsapp.properties` with your Twilio credentials

### Issue: "Failed to send message"
**Solutions:**
1. Verify you've joined the sandbox (send "join [code]" again)
2. Check Account SID and Auth Token are correct
3. Ensure your phone number includes country code
4. Check internet connection

### Issue: "Error 20003: Authentication Error"
**Solution:** Your Auth Token is incorrect. Get it from Twilio Console

### Issue: "Error 63016: Media: Unable to fetch"
**Solution:** PDF URL must be publicly accessible. Use cloud storage.

### Issue: Messages not received
**Solutions:**
1. Verify WhatsApp is installed and working
2. Check if you're using correct phone number format
3. In sandbox, ensure you've joined within last 24 hours
4. Check Twilio logs: [https://console.twilio.com/monitor/logs/](https://console.twilio.com/monitor/logs/)

---

## 📊 Usage Examples

### Example 1: Check Balance via WhatsApp
```batch
whatsapp-services.bat
> Select: 1 (Balance Inquiry)
> Enter PIN: 1234
> ✓ Message sent to WhatsApp!
```

### Example 2: Send Daily Reports
Create a scheduled task (Windows Task Scheduler):
```
Program: send-all-reports.bat
Trigger: Daily at 6:00 PM
```

### Example 3: Real-time Transaction Alerts
Modify transaction classes to auto-send:
```java
// After successful deposit/withdrawal
WhatsAppInteractive.sendTransactionAlert(pin, "Deposit", 5000);
```

---

## 📚 API Reference

### WhatsAppService Methods

```java
// Send simple text message
whatsAppService.sendMessage(String message)

// Send formatted report
whatsAppService.sendFormattedReport(String reportData)

// Send transaction alert
whatsAppService.sendTransactionAlert(String pin, String type, int amount, int balance)

// Send balance inquiry
whatsAppService.sendBalanceInquiry(String pin, int balance)

// Send PIN change confirmation
whatsAppService.sendPINChangeConfirmation(String cardNumber)

// Send PDF (requires public URL)
whatsAppService.sendPDF(String message, String pdfPath, String pdfUrl)
```

---

## 🔐 Security Notes

1. **Never commit credentials** - Keep `whatsapp.properties` in `.gitignore`
2. **Use environment variables** for production credentials
3. **Rotate tokens regularly** - Change Auth Token every 3 months
4. **Monitor usage** - Check Twilio dashboard for unauthorized use
5. **Rate limiting** - Add delays between bulk messages
6. **Validate inputs** - Sanitize all user inputs before sending

---

## 💰 Cost Optimization

### Sandbox (FREE)
- Perfect for development and testing
- Unlimited messages
- Up to 5 users can join sandbox

### Production Tips:
1. **Use templates** - Pre-approved templates are cheaper
2. **Batch messages** - Combine multiple notifications
3. **Set limits** - Max messages per day per user
4. **Use sessions** - 24-hour windows are free
5. **Monitor costs** - Set billing alerts in Twilio

---

## 📞 Support

### Twilio Support
- Documentation: [https://www.twilio.com/docs/whatsapp](https://www.twilio.com/docs/whatsapp)
- Support: [https://support.twilio.com/](https://support.twilio.com/)
- Community: [https://www.twilio.com/community](https://www.twilio.com/community)

### Application Support
- Check logs in Twilio Console
- Enable debug mode in `whatsapp.properties`:
  ```properties
  whatsapp.debug=true
  ```

---

## ✅ Checklist

Before going live:

- [ ] Twilio account created and verified
- [ ] Account SID and Auth Token obtained
- [ ] WhatsApp sandbox joined (for testing)
- [ ] `whatsapp.properties` configured with credentials
- [ ] Phone number format verified (with country code)
- [ ] Test message sent successfully
- [ ] All services compiled without errors
- [ ] Balance inquiry tested
- [ ] Transaction alert tested
- [ ] Reports sent successfully
- [ ] Error handling working
- [ ] Security measures implemented

---

## 🎉 You're All Set!

Your Bank Management System now supports WhatsApp messaging! 

**Next Steps:**
1. Test all features thoroughly
2. Apply for WhatsApp Business API (for production)
3. Set up automatic alerts for transactions
4. Configure scheduled reports
5. Monitor usage and costs

Happy messaging! 📱💬
