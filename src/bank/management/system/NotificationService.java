package bank.management.system;

import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Unified Notification Service
 * Handles sending notifications via Email and WhatsApp
 */
public class NotificationService {
    
    private EmailService emailService;
    private WhatsAppService whatsAppService;
    private Properties emailConfig;
    private Properties whatsAppConfig;
    
    public NotificationService() {
        try {
            this.emailService = new EmailService();
            this.whatsAppService = new WhatsAppService();
            this.emailConfig = loadEmailConfig();
            this.whatsAppConfig = loadWhatsAppConfig();
        } catch (Exception e) {
            System.err.println("Error initializing NotificationService: " + e.getMessage());
        }
    }
    
    /**
     * Send notification via both Email and WhatsApp
     */
    public boolean sendNotification(String recipient, String subject, String message) {
        boolean emailSent = false;
        boolean whatsappSent = false;
        
        try {
            emailSent = sendEmail(recipient, subject, message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
        
        try {
            whatsappSent = sendWhatsApp(recipient, message);
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp: " + e.getMessage());
        }
        
        return emailSent || whatsappSent;
    }
    
    /**
     * Send notification via Email only
     */
    public boolean sendEmail(String recipient, String subject, String message) throws Exception {
        try {
            emailService.sendEmail(recipient, subject, message);
            System.out.println("Email sent successfully to: " + recipient);
            return true;
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send notification via WhatsApp only
     */
    public boolean sendWhatsApp(String recipient, String message) throws Exception {
        try {
            whatsAppService.sendMessage(recipient, message);
            System.out.println("WhatsApp message sent successfully to: " + recipient);
            return true;
        } catch (Exception e) {
            System.err.println("WhatsApp sending failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send HTML formatted email
     */
    public boolean sendHTMLEmail(String recipient, String subject, String htmlMessage) throws Exception {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", emailConfig.getProperty("mail.smtp.host"));
            props.put("mail.smtp.port", emailConfig.getProperty("mail.smtp.port"));
            props.put("mail.smtp.auth", emailConfig.getProperty("mail.smtp.auth"));
            props.put("mail.smtp.starttls.enable", emailConfig.getProperty("mail.smtp.starttls.enable"));
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        emailConfig.getProperty("mail.sender.email"),
                        emailConfig.getProperty("mail.sender.password")
                    );
                }
            });
            
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailConfig.getProperty("mail.sender.email")));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            msg.setSubject(subject);
            msg.setContent(htmlMessage, "text/html; charset=utf-8");
            
            Transport.send(msg);
            System.out.println("HTML Email sent successfully to: " + recipient);
            return true;
        } catch (Exception e) {
            System.err.println("HTML Email sending failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Send bulk notifications
     */
    public int sendBulkNotifications(String[] recipients, String subject, String message) {
        int successCount = 0;
        for (String recipient : recipients) {
            try {
                if (sendNotification(recipient, subject, message)) {
                    successCount++;
                }
            } catch (Exception e) {
                System.err.println("Failed to send notification to " + recipient + ": " + e.getMessage());
            }
        }
        return successCount;
    }
    
    /**
     * Send alert notification
     */
    public boolean sendAlert(String recipient, String alertType, String alertMessage) {
        String subject = "🚨 Bank Alert: " + alertType;
        String message = "Alert Type: " + alertType + "\n" +
                        "Message: " + alertMessage + "\n" +
                        "Time: " + new java.time.LocalDateTime.now();
        return sendNotification(recipient, subject, message);
    }
    
    /**
     * Send transaction notification
     */
    public boolean sendTransactionNotification(String recipient, String transactionType, 
                                              double amount, String accountNumber) {
        String subject = "Transaction Notification - " + transactionType;
        String message = "Transaction Type: " + transactionType + "\n" +
                        "Amount: ₹" + amount + "\n" +
                        "Account: " + accountNumber + "\n" +
                        "Time: " + new java.time.LocalDateTime.now();
        return sendNotification(recipient, subject, message);
    }
    
    /**
     * Load email configuration
     */
    private Properties loadEmailConfig() throws IOException {
        Properties props = new Properties();
        try {
            props.load(NotificationService.class.getClassLoader()
                    .getResourceAsStream("email.properties"));
        } catch (Exception e) {
            System.err.println("Could not load email.properties: " + e.getMessage());
        }
        return props;
    }
    
    /**
     * Load WhatsApp configuration
     */
    private Properties loadWhatsAppConfig() throws IOException {
        Properties props = new Properties();
        try {
            props.load(NotificationService.class.getClassLoader()
                    .getResourceAsStream("whatsapp.properties"));
        } catch (Exception e) {
            System.err.println("Could not load whatsapp.properties: " + e.getMessage());
        }
        return props;
    }
    
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        
        // Example: Send test notification
        try {
            service.sendAlert(
                "YOUR_EMAIL@gmail.com",
                "Test Alert",
                "This is a test notification from Bank Management System"
            );
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
}
