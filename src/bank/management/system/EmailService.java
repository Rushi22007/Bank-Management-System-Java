package bank.management.system;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
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
    
    /**
     * Send email with PDF attachment
     * @param messageBody The content of the email
     * @param pdfFilePath The path to the PDF file to attach
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmailWithPDF(String messageBody, String pdfFilePath) {
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
        
        // Check if PDF file exists
        File pdfFile = new File(pdfFilePath);
        if (!pdfFile.exists()) {
            System.err.println("Error: PDF file not found at " + pdfFilePath);
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
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(messageBody);
            multipart.addBodyPart(textPart);
            
            // Add PDF attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(pdfFile);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(pdfFile.getName());
            multipart.addBodyPart(attachmentPart);
            
            // Set content
            message.setContent(multipart);
            
            // Send email
            Transport.send(message);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    EMAIL SENT SUCCESSFULLY!                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nEmail sent to: " + recipientEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Attachment: " + pdfFile.getName() + " (" + formatFileSize(pdfFile.length()) + ")\n");
            
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
            System.err.println("4. Check your internet connection");
            System.err.println("5. Ensure the PDF file is not corrupted\n");
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Format file size in human-readable format
     * @param size File size in bytes
     * @return Formatted file size string
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}
