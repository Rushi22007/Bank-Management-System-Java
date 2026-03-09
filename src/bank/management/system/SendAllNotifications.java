package bank.management.system;

/**
 * Send All Notifications Demo
 * Demonstrates sending all notification types via both Email and WhatsApp immediately
 */
public class SendAllNotifications {
    
    private static final NotificationService notificationService = new NotificationService();
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("🚀 Bank Management System - Notification Demo");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        try {
            // Example contact information (replace with actual values)
            String email = "customer@example.com";
            String whatsappNumber = "+919876543210";
            String customerName = "Abhishek Kumar";
            String accountNumber = "123456789012";
            
            System.out.println("📋 Starting to send all notification types...\n");
            
            // 1. Send Welcome Notification
            sendWelcomeNotification(email, whatsappNumber, customerName, accountNumber);
            
            // 2. Send Transaction Confirmation
            sendTransactionNotification(email, whatsappNumber, accountNumber);
            
            // 3. Send Account Alert
            sendAccountAlert(email, whatsappNumber, customerName);
            
            // 4. Send OTP Notification
            sendOTPNotification(email, whatsappNumber);
            
            // 5. Send Account Statement
            sendAccountStatement(email, whatsappNumber, accountNumber, customerName);
            
            // 6. Send Custom Message
            sendCustomMessages(email, whatsappNumber);
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("✅ All notifications queued and sent!");
            System.out.println("═══════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.err.println("❌ Error sending notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 1. Send Welcome Notification
     */
    private static void sendWelcomeNotification(String email, String whatsappNumber, 
                                               String customerName, String accountNumber) {
        System.out.println("\n1️⃣ WELCOME NOTIFICATION");
        System.out.println("────────────────────────────────────────");
        
        try {
            // Format welcome message
            String welcomeMessage = MessageFormatter.formatWelcomeMessage(customerName, accountNumber);
            
            // Send via both channels
            notificationService.sendEmail(
                email,
                "👋 Welcome to Bank Management System!",
                welcomeMessage
            );
            System.out.println("✅ Welcome email sent to: " + email);
            
            notificationService.sendWhatsApp(whatsappNumber, welcomeMessage);
            System.out.println("✅ Welcome WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome notification: " + e.getMessage());
        }
    }
    
    /**
     * 2. Send Transaction Confirmation
     */
    private static void sendTransactionNotification(String email, String whatsappNumber, 
                                                    String accountNumber) {
        System.out.println("\n2️⃣ TRANSACTION CONFIRMATION");
        System.out.println("────────────────────────────────────────");
        
        try {
            // Sample transaction details
            String transactionType = "Deposit";
            double amount = 5000.00;
            double balance = 25000.00;
            
            // Send via Email
            String transactionMessage = MessageFormatter.formatTransactionConfirmation(
                transactionType, amount, accountNumber, String.valueOf(balance)
            );
            
            notificationService.sendEmail(
                email,
                "💰 Transaction Confirmation - " + transactionType,
                transactionMessage
            );
            System.out.println("✅ Transaction confirmation email sent to: " + email);
            
            // Send via WhatsApp with WhatsApp format
            String whatsappTxnMsg = MessageFormatter.formatWhatsAppMessage(
                "💰 TRANSACTION CONFIRMED",
                "*Type:* " + transactionType + "\n" +
                "*Amount:* ₹" + String.format("%.2f", amount) + "\n" +
                "*Account:* " + accountNumber + "\n" +
                "*New Balance:* ₹" + String.format("%.2f", balance)
            );
            
            notificationService.sendWhatsApp(whatsappNumber, whatsappTxnMsg);
            System.out.println("✅ Transaction confirmation WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send transaction notification: " + e.getMessage());
        }
    }
    
    /**
     * 3. Send Account Alert
     */
    private static void sendAccountAlert(String email, String whatsappNumber, String customerName) {
        System.out.println("\n3️⃣ ACCOUNT ALERT");
        System.out.println("────────────────────────────────────────");
        
        try {
            String alertType = "Unusual Activity Detected";
            String description = "Login from new device detected at 02:30 AM from IP: 203.0.113.45";
            String severity = "WARNING";
            
            // Format alert message
            String alertMessage = MessageFormatter.formatAlertMessage(alertType, description, severity);
            
            // Send via Email
            notificationService.sendEmail(
                email,
                "⚠️ SECURITY ALERT - Account Activity",
                alertMessage
            );
            System.out.println("✅ Alert email sent to: " + email);
            
            // Send via WhatsApp
            notificationService.sendWhatsApp(whatsappNumber, alertMessage);
            System.out.println("✅ Alert WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send alert: " + e.getMessage());
        }
    }
    
    /**
     * 4. Send OTP Notification
     */
    private static void sendOTPNotification(String email, String whatsappNumber) {
        System.out.println("\n4️⃣ OTP NOTIFICATION");
        System.out.println("────────────────────────────────────────");
        
        try {
            String otp = "654321";
            int expiryMinutes = 5;
            
            // Format OTP message
            String otpMessage = MessageFormatter.formatOTPMessage(otp, expiryMinutes);
            
            // Send via Email
            notificationService.sendEmail(
                email,
                "🔐 Your One-Time Password (OTP)",
                otpMessage
            );
            System.out.println("✅ OTP email sent to: " + email);
            
            // Send via WhatsApp
            notificationService.sendWhatsApp(whatsappNumber, otpMessage);
            System.out.println("✅ OTP WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP notification: " + e.getMessage());
        }
    }
    
    /**
     * 5. Send Account Statement
     */
    private static void sendAccountStatement(String email, String whatsappNumber, 
                                            String accountNumber, String customerName) {
        System.out.println("\n5️⃣ ACCOUNT STATEMENT");
        System.out.println("────────────────────────────────────────");
        
        try {
            double balance = 25000.00;
            int transactionCount = 12;
            String lastTransaction = "Deposit of ₹5000 - 1 hour ago";
            
            // Format statement
            String statement = MessageFormatter.formatAccountStatement(
                accountNumber, balance, transactionCount, lastTransaction
            );
            
            // Create HTML email version
            String htmlStatement = MessageFormatter.formatHTMLEmail(
                "Account Statement",
                "<h2>Account Summary for: " + customerName + "</h2>" +
                "<p><strong>Account Number:</strong> " + maskAccountNumber(accountNumber) + "</p>" +
                "<p><strong>Current Balance:</strong> ₹" + String.format("%.2f", balance) + "</p>" +
                "<p><strong>Total Transactions:</strong> " + transactionCount + "</p>" +
                "<p><strong>Last Transaction:</strong> " + lastTransaction + "</p>" +
                "<p><strong>Generated:</strong> " + java.time.LocalDateTime.now() + "</p>",
                "Please keep this statement confidential"
            );
            
            // Send via Email (HTML format)
            notificationService.sendHTMLEmail(
                email,
                "📊 Your Account Statement",
                htmlStatement
            );
            System.out.println("✅ Statement HTML email sent to: " + email);
            
            // Send via WhatsApp
            notificationService.sendWhatsApp(whatsappNumber, statement);
            System.out.println("✅ Statement WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send account statement: " + e.getMessage());
        }
    }
    
    /**
     * 6. Send Custom Messages
     */
    private static void sendCustomMessages(String email, String whatsappNumber) {
        System.out.println("\n6️⃣ CUSTOM MESSAGES");
        System.out.println("────────────────────────────────────────");
        
        try {
            // Custom promotional message
            String promoMessage = "🎉 *Special Offer Alert!*\n\n" +
                                 "Get *5% cashback* on all deposits this week!\n" +
                                 "Offer valid till March 15, 2026\n\n" +
                                 "_Exclusive for our valued customers_";
            
            notificationService.sendEmail(
                email,
                "🎉 Special Promotional Offer",
                promoMessage
            );
            System.out.println("✅ Promotional email sent to: " + email);
            
            notificationService.sendWhatsApp(whatsappNumber, promoMessage);
            System.out.println("✅ Promotional WhatsApp sent to: " + whatsappNumber);
            
            // Custom reminder message
            String reminderMessage = "📌 *Important Reminder*\n\n" +
                                    "• Verify your email address\n" +
                                    "• Update your emergency contact\n" +
                                    "• Enable 2-Factor Authentication\n\n" +
                                    "_Secure your account today!_";
            
            notificationService.sendEmail(
                email,
                "📌 Account Security Reminder",
                reminderMessage
            );
            System.out.println("✅ Reminder email sent to: " + email);
            
            notificationService.sendWhatsApp(whatsappNumber, reminderMessage);
            System.out.println("✅ Reminder WhatsApp sent to: " + whatsappNumber);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send custom messages: " + e.getMessage());
        }
    }
    
    /**
     * Mask account number for security
     */
    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        int length = accountNumber.length();
        return "*".repeat(Math.max(0, length - 4)) + 
               accountNumber.substring(Math.max(0, length - 4));
    }
}
