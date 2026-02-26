package bank.management.system;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EmailService {
    private Properties emailConfig;
    private String senderEmail;
    private String senderPassword;
    private String recipientEmail;
    private String fromName;
    private String subject;
    
    public EmailService() {
        loadConfiguration();
    }
    
    /**
     * Load email configuration from email.properties file
     */
    private void loadConfiguration() {
        emailConfig = new Properties();
        try (FileInputStream fis = new FileInputStream("email.properties")) {
            emailConfig.load(fis);
            
            senderEmail = emailConfig.getProperty("mail.sender.email");
            senderPassword = emailConfig.getProperty("mail.sender.password");
            recipientEmail = emailConfig.getProperty("mail.recipient.email");
            fromName = emailConfig.getProperty("mail.from.name", "Bank Management System");
            subject = emailConfig.getProperty("mail.subject", "Database Report");
            
        } catch (IOException e) {
            System.err.println("Error loading email configuration: " + e.getMessage());
            System.err.println("Please ensure email.properties file exists in the project root.");
        }
    }
    
    /**
     * Send email with the provided message content
     * @param messageBody The content of the email
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmail(String messageBody) {
        // Validate configuration
        if (senderEmail == null || senderPassword == null || recipientEmail == null) {
            System.err.println("Email configuration is incomplete. Please check email.properties file.");
            return false;
        }
        
        if (senderEmail.equals("your-email@gmail.com")) {
            System.err.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.err.println("║                    EMAIL NOT CONFIGURED                            ║");
            System.err.println("╚════════════════════════════════════════════════════════════════════╝");
            System.err.println("\nPlease configure your email settings in email.properties file:");
            System.err.println("1. Set mail.sender.email to your Gmail address");
            System.err.println("2. Set mail.sender.password to your Gmail App Password");
            System.err.println("   (Generate App Password: https://myaccount.google.com/apppasswords)");
            System.err.println("3. Set mail.recipient.email to the recipient's email address\n");
            return false;
        }
        
        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getProperty("mail.smtp.host"));
        props.put("mail.smtp.port", emailConfig.getProperty("mail.smtp.port"));
        props.put("mail.smtp.auth", emailConfig.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", emailConfig.getProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.starttls.required", emailConfig.getProperty("mail.smtp.starttls.required", "true"));
        props.put("mail.smtp.ssl.protocols", emailConfig.getProperty("mail.smtp.ssl.protocols", "TLSv1.2"));
        props.put("mail.smtp.ssl.trust", emailConfig.getProperty("mail.smtp.ssl.trust", "smtp.gmail.com"));
        
        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        
        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);
            
            // Send email
            Transport.send(message);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    EMAIL SENT SUCCESSFULLY!                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nEmail sent to: " + recipientEmail);
            System.out.println("Subject: " + subject + "\n");
            
            return true;
            
        } catch (MessagingException e) {
            System.err.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.err.println("║                    EMAIL SENDING FAILED                            ║");
            System.err.println("╚════════════════════════════════════════════════════════════════════╝");
            System.err.println("\nError: " + e.getMessage());
            System.err.println("\nCommon issues:");
            System.err.println("1. Check if you're using an App Password (not regular Gmail password)");
            System.err.println("2. Ensure 2-Step Verification is enabled on your Gmail account");
            System.err.println("3. Verify your email and password in email.properties");
            System.err.println("4. Check your internet connection\n");
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send email to a custom recipient
     * @param messageBody The content of the email
     * @param customRecipient The recipient email address
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmail(String messageBody, String customRecipient) {
        String originalRecipient = this.recipientEmail;
        this.recipientEmail = customRecipient;
        boolean result = sendEmail(messageBody);
        this.recipientEmail = originalRecipient;
        return result;
    }
}
