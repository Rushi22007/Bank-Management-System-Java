package bank.management.system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message Formatter Utility
 * Formats messages for Email and WhatsApp notifications
 */
public class MessageFormatter {
    
    private static final DateTimeFormatter DATE_FORMAT = 
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    /**
     * Format transaction confirmation message
     */
    public static String formatTransactionConfirmation(String transactionType, 
                                                       double amount, 
                                                       String accountNumber,
                                                       String balance) {
        StringBuilder message = new StringBuilder();
        message.append("═══════════════════════════════════════\n");
        message.append("🏦 BANK MANAGEMENT SYSTEM\n");
        message.append("═══════════════════════════════════════\n\n");
        message.append("✅ Transaction Confirmed\n\n");
        message.append("Transaction Type: ").append(transactionType).append("\n");
        message.append("Amount: ₹").append(String.format("%.2f", amount)).append("\n");
        message.append("Account: ").append(maskAccountNumber(accountNumber)).append("\n");
        message.append("Balance: ₹").append(String.format("%.2f", Double.parseDouble(balance))).append("\n");
        message.append("Date: ").append(getCurrentDateTime()).append("\n");
        message.append("═══════════════════════════════════════\n");
        
        return message.toString();
    }
    
    /**
     * Format HTML email template
     */
    public static String formatHTMLEmail(String title, String content, String footerMessage) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; }\n");
        html.append(".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; }\n");
        html.append(".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; border-radius: 8px; }\n");
        html.append(".content { padding: 20px; line-height: 1.6; }\n");
        html.append(".footer { background-color: #ecf0f1; padding: 15px; text-align: center; font-size: 12px; }\n");
        html.append(".highlight { background-color: #fff3cd; padding: 10px; border-left: 4px solid #ffc107; margin: 10px 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class='container'>\n");
        html.append("<div class='header'>\n");
        html.append("<h1>🏦 ").append(title).append("</h1>\n");
        html.append("</div>\n");
        html.append("<div class='content'>\n");
        html.append(content).append("\n");
        html.append("</div>\n");
        html.append("<div class='footer'>\n");
        html.append(footerMessage).append("<br>\n");
        html.append("Bank Management System - Automated Notification\n");
        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }
    
    /**
     * Format WhatsApp message with emoji
     */
    public static String formatWhatsAppMessage(String title, String details) {
        StringBuilder message = new StringBuilder();
        message.append("*").append(title).append("*\n");
        message.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        message.append(details);
        message.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        message.append("_Automated Message from Bank System_");
        
        return message.toString();
    }
    
    /**
     * Format alert message
     */
    public static String formatAlertMessage(String alertType, String description, String severity) {
        StringBuilder message = new StringBuilder();
        
        String emoji = getAlertEmoji(severity);
        
        message.append(emoji).append(" *").append(severity.toUpperCase()).append(" ALERT*\n\n");
        message.append("*Type:* ").append(alertType).append("\n");
        message.append("*Description:* ").append(description).append("\n");
        message.append("*Time:* ").append(getCurrentDateTime()).append("\n");
        message.append("*Action:* Please review your account immediately\n");
        
        return message.toString();
    }
    
    /**
     * Format account statement
     */
    public static String formatAccountStatement(String accountNumber, double balance, 
                                               int transactionCount, String lastTransaction) {
        StringBuilder message = new StringBuilder();
        message.append("*📊 ACCOUNT STATEMENT*\n\n");
        message.append("Account: ").append(maskAccountNumber(accountNumber)).append("\n");
        message.append("Balance: ₹").append(String.format("%.2f", balance)).append("\n");
        message.append("Transactions: ").append(transactionCount).append("\n");
        message.append("Last Transaction: ").append(lastTransaction).append("\n");
        message.append("Generated: ").append(getCurrentDateTime()).append("\n");
        
        return message.toString();
    }
    
    /**
     * Format OTP message
     */
    public static String formatOTPMessage(String otp, int expiryMinutes) {
        StringBuilder message = new StringBuilder();
        message.append("*🔐 SECURITY CODE*\n\n");
        message.append("Your One-Time Password (OTP): *").append(otp).append("*\n\n");
        message.append("⏱️ Valid for: ").append(expiryMinutes).append(" minutes\n");
        message.append("⚠️ Never share this code with anyone\n\n");
        message.append("Generated: ").append(getCurrentDateTime()).append("\n");
        
        return message.toString();
    }
    
    /**
     * Format welcome message for new account
     */
    public static String formatWelcomeMessage(String customerName, String accountNumber) {
        StringBuilder message = new StringBuilder();
        message.append("*👋 Welcome to Bank Management System!*\n\n");
        message.append("Dear ").append(customerName).append(",\n\n");
        message.append("Your account has been successfully created!\n\n");
        message.append("*Account Details:*\n");
        message.append("Account Number: ").append(maskAccountNumber(accountNumber)).append("\n\n");
        message.append("You can now:\n");
        message.append("✅ Deposit money\n");
        message.append("✅ Withdraw money\n");
        message.append("✅ Check balance\n");
        message.append("✅ View statements\n");
        message.append("✅ Fast cash withdrawal\n\n");
        message.append("Thank you for banking with us!\n");
        
        return message.toString();
    }
    
    /**
     * Mask sensitive account number
     */
    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        int length = accountNumber.length();
        return "*".repeat(Math.max(0, length - 4)) + 
               accountNumber.substring(Math.max(0, length - 4));
    }
    
    /**
     * Get alert emoji based on severity
     */
    private static String getAlertEmoji(String severity) {
        return switch(severity.toLowerCase()) {
            case "critical" -> "🔴";
            case "warning" -> "🟡";
            case "info" -> "🔵";
            default -> "⚪";
        };
    }
    
    /**
     * Get current date and time
     */
    private static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_FORMAT);
    }
    
    /**
     * Format currency amount
     */
    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }
    
    /**
     * Format date
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMAT);
    }
}
