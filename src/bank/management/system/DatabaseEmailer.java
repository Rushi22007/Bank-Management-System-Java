package bank.management.system;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseEmailer {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static final String PDF_OUTPUT_PATH = "reports/database_report.pdf";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║       BANK MANAGEMENT SYSTEM - DATABASE EMAIL SENDER              ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            System.out.println("Generating PDF report with comprehensive sections...\n");
            
            // Create reports directory if it doesn't exist
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
                System.out.println("✓ Created reports directory");
            }
            
            // Generate PDF report
            PDFReportGenerator pdfGenerator = new PDFReportGenerator();
            boolean pdfGenerated = pdfGenerator.generateReport(PDF_OUTPUT_PATH);
            
            if (!pdfGenerated) {
                System.err.println("\nError: Failed to generate PDF report.");
                System.exit(1);
            }
            
            System.out.println("✓ PDF report generated successfully");
            System.out.println("✓ Location: " + PDF_OUTPUT_PATH);
            
            // Get file size
            File pdfFile = new File(PDF_OUTPUT_PATH);
            String fileSize = formatFileSize(pdfFile.length());
            System.out.println("✓ File size: " + fileSize);
            
            // Generate email message
            String emailMessage = generateEmailMessage();
            
            // Send email with PDF attachment
            System.out.println("\nSending email with PDF attachment...\n");
            EmailService emailService = new EmailService();
            boolean success = emailService.sendEmailWithPDF(emailMessage, PDF_OUTPUT_PATH);
            
            if (!success) {
                System.err.println("\nThe PDF report was generated but could not be sent via email.");
                System.err.println("You can find the report at: " + PDF_OUTPUT_PATH);
                System.err.println("Please configure email.properties and try again.");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate email message body
     * @return Email message
     */
    private static String generateEmailMessage() {
        StringBuilder message = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' hh:mm:ss a");
        
        message.append("Dear Administrator,\n\n");
        message.append("Please find attached the comprehensive database report for the Bank Management System.\n\n");
        message.append("Report Details:\n");
        message.append("═══════════════════════════════════════════════════════════════════\n");
        message.append("Generated: ").append(dateFormat.format(new Date())).append("\n");
        message.append("Format: PDF (Portable Document Format)\n");
        message.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        message.append("The report includes the following sections:\n");
        message.append("  1. Executive Summary - Overview of key metrics\n");
        message.append("  2. Registered Users - Complete user information\n");
        message.append("  3. Additional User Details - Extended user data\n");
        message.append("  4. Active Accounts - Account details and status\n");
        message.append("  5. Transaction History - Complete transaction log\n");
        message.append("  6. Account Balances - Current balances for all accounts\n");
        message.append("  7. Statistics & Analytics - System-wide analytics\n\n");
        
        message.append("This report is confidential and intended for authorized personnel only.\n\n");
        message.append("Best regards,\n");
        message.append("Bank Management System\n");
        message.append("Automated Report Generation Service\n");
        
        return message.toString();
    }
    
    /**
     * Format file size in human-readable format
     * @param size File size in bytes
     * @return Formatted file size string
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}
