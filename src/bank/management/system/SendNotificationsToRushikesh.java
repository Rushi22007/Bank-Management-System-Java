package bank.management.system;

/**
 * Send Notifications to Rushikesh
 * Sends email and WhatsApp notifications to specified contact
 */
public class SendNotificationsToRushikesh {
    
    private static final NotificationService notificationService = new NotificationService();
    
    // User's contact information
    private static final String EMAIL = "rushikeshchamale5@gmail.com";
    private static final String WHATSAPP_NUMBER = "+91 8767599309";
    private static final String CUSTOMER_NAME = "Rushikesh";
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("📧 Sending Email and WhatsApp Notifications");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        System.out.println("📬 Recipient Details:");
        System.out.println("   Email: " + EMAIL);
        System.out.println("   WhatsApp: " + WHATSAPP_NUMBER);
        System.out.println("   Name: " + CUSTOMER_NAME);
        System.out.println("\n");
        
        try {
            // Send multiple notifications
            sendTransactionNotification();
            Thread.sleep(1000);
            
            sendAccountAlert();
            Thread.sleep(1000);
            
            sendWelcomeNotification();
            Thread.sleep(1000);
            
            sendCustomMessages();
            
            System.out.println("\n═══════════════════════════════════════════════════════");
            System.out.println("✅ All notifications sent successfully!");
            System.out.println("═══════════════════════════════════════════════════════");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send Transaction Notification
     */
    private static void sendTransactionNotification() throws Exception {
        System.out.println("\n1️⃣ SENDING TRANSACTION NOTIFICATION");
        System.out.println("────────────────────────────────────────");
        
        try {
            String subject = "💰 Transaction Confirmation - Deposit";
            String message = "✅ Transaction Confirmed\n\n" +
                            "Transaction Type: Deposit\n" +
                            "Amount: ₹5,000.00\n" +
                            "Account: XX5309\n" +
                            "Balance: ₹25,000.00\n" +
                            "Date & Time: " + java.time.LocalDateTime.now() + "\n\n" +
                            "Thank you for using Bank Management System!";
            
            // Send Email
            notificationService.sendEmail(EMAIL, subject, message);
            System.out.println("✅ Email sent successfully!");
            
            // Send WhatsApp
            String whatsappMessage = "💰 *TRANSACTION CONFIRMED*\n\n" +
                                    "*Type:* Deposit\n" +
                                    "*Amount:* ₹5,000.00\n" +
                                    "*Account:* XX5309\n" +
                                    "*Balance:* ₹25,000.00\n\n" +
                                    "_Thank you for banking with us!_";
            
            notificationService.sendWhatsApp(WHATSAPP_NUMBER, whatsappMessage);
            System.out.println("✅ WhatsApp message sent successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send transaction notification: " + e.getMessage());
        }
    }
    
    /**
     * Send Account Alert
     */
    private static void sendAccountAlert() throws Exception {
        System.out.println("\n2️⃣ SENDING ACCOUNT ALERT");
        System.out.println("────────────────────────────────────────");
        
        try {
            String subject = "⚠️ SECURITY ALERT - Account Activity";
            String message = "⚠️ IMPORTANT SECURITY ALERT\n\n" +
                            "Alert Type: Unusual Activity Detected\n" +
                            "Description: Login from new device\n" +
                            "Time: " + java.time.LocalDateTime.now() + "\n\n" +
                            "If this wasn't you, please contact support immediately!";
            
            // Send Email
            notificationService.sendEmail(EMAIL, subject, message);
            System.out.println("✅ Alert email sent successfully!");
            
            // Send WhatsApp
            String whatsappAlert = "🟡 *WARNING ALERT*\n\n" +
                                  "*Type:* Unusual Activity Detected\n" +
                                  "*Description:* Login from new device\n" +
                                  "*Time:* " + java.time.LocalDateTime.now() + "\n\n" +
                                  "⚠️ If this wasn't you, contact support immediately!";
            
            notificationService.sendWhatsApp(WHATSAPP_NUMBER, whatsappAlert);
            System.out.println("✅ Alert WhatsApp sent successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send alert: " + e.getMessage());
        }
    }
    
    /**
     * Send Welcome Notification
     */
    private static void sendWelcomeNotification() throws Exception {
        System.out.println("\n3️⃣ SENDING WELCOME NOTIFICATION");
        System.out.println("────────────────────────────────────────");
        
        try {
            String subject = "👋 Welcome to Bank Management System!";
            String message = "Hello " + CUSTOMER_NAME + ",\n\n" +
                            "Welcome to Bank Management System!\n\n" +
                            "Your account is now active and ready to use.\n\n" +
                            "You can now:\n" +
                            "✅ Deposit money\n" +
                            "✅ Withdraw money\n" +
                            "✅ Check balance\n" +
                            "✅ View statements\n" +
                            "✅ Fast cash withdrawal\n\n" +
                            "Thank you for banking with us!";
            
            // Send Email
            notificationService.sendEmail(EMAIL, subject, message);
            System.out.println("✅ Welcome email sent successfully!");
            
            // Send WhatsApp
            String whatsappWelcome = "👋 *Welcome to Bank Management System!*\n\n" +
                                    "Hello " + CUSTOMER_NAME + ",\n\n" +
                                    "Your account is now active!\n\n" +
                                    "You can now:\n" +
                                    "✅ Deposit money\n" +
                                    "✅ Withdraw money\n" +
                                    "✅ Check balance\n" +
                                    "✅ View statements\n" +
                                    "✅ Fast cash\n\n" +
                                    "_Thank you for banking with us!_";
            
            notificationService.sendWhatsApp(WHATSAPP_NUMBER, whatsappWelcome);
            System.out.println("✅ Welcome WhatsApp sent successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome notification: " + e.getMessage());
        }
    }
    
    /**
     * Send Custom Messages
     */
    private static void sendCustomMessages() throws Exception {
        System.out.println("\n4️⃣ SENDING CUSTOM MESSAGES");
        System.out.println("────────────────────────────────────────");
        
        try {
            // Custom message 1
            String customSubject = "🎉 Exclusive Offer for You";
            String customMessage = "Dear " + CUSTOMER_NAME + ",\n\n" +
                                  "🎉 Special Offer Alert!\n\n" +
                                  "Get 5% cashback on all deposits this week!\n" +
                                  "Offer valid till March 15, 2026\n\n" +
                                  "Don't miss this exclusive opportunity!\n\n" +
                                  "Best regards,\n" +
                                  "Bank Management System Team";
            
            notificationService.sendEmail(EMAIL, customSubject, customMessage);
            System.out.println("✅ Promotional email sent successfully!");
            
            String whatsappPromo = "🎉 *EXCLUSIVE OFFER*\n\n" +
                                  "Get *5% cashback* on all deposits this week!\n" +
                                  "Offer valid till March 15, 2026\n\n" +
                                  "_Don't miss this opportunity!_";
            
            notificationService.sendWhatsApp(WHATSAPP_NUMBER, whatsappPromo);
            System.out.println("✅ Promotional WhatsApp sent successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send custom messages: " + e.getMessage());
        }
    }
}
