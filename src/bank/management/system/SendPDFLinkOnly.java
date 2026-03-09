package bank.management.system;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Send PDF download link via WhatsApp as plain text (no media attachment)
 * More reliable for sandbox accounts
 */
public class SendPDFLinkOnly {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static final String PDF_OUTPUT_PATH = "reports/database_report.pdf";
    
    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║      WHATSAPP PDF LINK SENDER (TEXT ONLY)                         ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            WhatsAppService whatsAppService = new WhatsAppService();
            
            // Step 1: Generate PDF
            System.out.println("Step 1: Generating PDF report...");
            PDFReportGenerator pdfGenerator = new PDFReportGenerator();
            boolean pdfGenerated = pdfGenerator.generateReport(PDF_OUTPUT_PATH);
            if (!pdfGenerated) {
                System.err.println("✗ Failed to generate PDF report");
                return;
            }
            
            File pdfFile = new File(PDF_OUTPUT_PATH);
            String fileSize = formatFileSize(pdfFile.length());
            
            System.out.println("✓ PDF generated: " + fileSize);
            
            // Step 2: Upload PDF
            System.out.println("\nStep 2: Uploading PDF to cloud storage...");
            String pdfUrl = PDFUploader.uploadPDF(PDF_OUTPUT_PATH);
            
            if (pdfUrl == null) {
                System.err.println("✗ Could not upload PDF");
                return;
            }
            
            System.out.println("✓ PDF uploaded: " + pdfUrl);
            
            // Step 3: Send link as text message
            System.out.println("\nStep 3: Sending download link via WhatsApp (TEXT ONLY)...\n");
            
            String message = generateTextMessage(fileSize, pdfUrl);
            boolean success = whatsAppService.sendMessage(message);
            
            if (success) {
                System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("║          DOWNLOAD LINK SENT SUCCESSFULLY!                          ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════╝");
                System.out.println("\n✓ Sent to: +918767599309");
                System.out.println("✓ Message contains clickable download link");
                System.out.println("✓ Link: " + pdfUrl);
                System.out.println("\nCheck your WhatsApp for the message with download button!\n");
            } else {
                System.err.println("\n✗ Failed to send message");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateTextMessage(String fileSize, String pdfUrl) {
        StringBuilder msg = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        msg.append("📊 *BANK DATABASE REPORT*\n\n");
        msg.append("Your comprehensive PDF report is ready!\n\n");
        msg.append("📦 Size: ").append(fileSize).append("\n");
        msg.append("🕐 Generated: ").append(sdf.format(new Date())).append("\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            int users = getCount(conn, "SELECT COUNT(*) FROM signup");
            int accounts = getCount(conn, "SELECT COUNT(*) FROM account");
            int transactions = getCount(conn, "SELECT COUNT(*) FROM transactions");
            
            msg.append("*Report Includes:*\n");
            msg.append("• ").append(users).append(" Users\n");
            msg.append("• ").append(accounts).append(" Accounts\n");
            msg.append("• ").append(transactions).append(" Transactions\n");
            msg.append("• Balance Summary\n");
            msg.append("• Statistical Analysis\n\n");
            
        } catch (SQLException e) {
            msg.append("Complete database information\n\n");
        }
        
        msg.append("📥 *DOWNLOAD PDF:*\n");
        msg.append(pdfUrl).append("\n\n");
        msg.append("_Click the link above to download_\n");
        msg.append("⏰ Link valid for 14 days");
        
        return msg.toString();
    }
    
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
    
    private static int getCount(Connection conn, String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // Ignore
        }
        return 0;
    }
}
