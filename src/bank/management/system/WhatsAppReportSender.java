package bank.management.system;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * WhatsApp Report Sender
 * Sends database reports via WhatsApp
 */
public class WhatsAppReportSender {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static final String PDF_OUTPUT_PATH = "reports/database_report.pdf";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║      BANK MANAGEMENT SYSTEM - WHATSAPP PDF REPORT SENDER          ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            
            // Initialize WhatsApp service
            WhatsAppService whatsAppService = new WhatsAppService();
            
            if (!whatsAppService.isEnabled()) {
                System.err.println("WhatsApp service is not properly configured.");
                System.err.println("Please check whatsapp.properties file.");
                System.exit(1);
            }
            
            // Step 1: Generate PDF Report
            System.out.println("Step 1: Generating PDF report...");
            
            // Create reports directory if it doesn't exist
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
                System.out.println("✓ Created reports directory");
            }
            
            // Generate PDF
            PDFReportGenerator pdfGenerator = new PDFReportGenerator();
            boolean pdfGenerated = pdfGenerator.generateReport(PDF_OUTPUT_PATH);
            
            if (!pdfGenerated) {
                System.err.println("\nError: Failed to generate PDF report.");
                System.exit(1);
            }
            
            File pdfFile = new File(PDF_OUTPUT_PATH);
            String fileSize = formatFileSize(pdfFile.length());
            
            System.out.println("✓ PDF report generated successfully");
            System.out.println("✓ Location: " + PDF_OUTPUT_PATH);
            System.out.println("✓ File size: " + fileSize);
            
            // Step 2: Upload PDF to get public URL
            System.out.println("\nStep 2: Uploading PDF to cloud storage...");
            String pdfUrl = PDFUploader.uploadPDF(PDF_OUTPUT_PATH);
            
            if (pdfUrl == null) {
                System.err.println("\n⚠ Could not upload PDF automatically.");
                System.err.println("Sending notification without PDF attachment...\n");
                
                // Send notification without PDF
                String summary = generatePDFNotificationMessage(fileSize);
                whatsAppService.sendFormattedReport(summary);
                
                System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("║                   PDF REPORT GENERATED                             ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════╝");
                System.out.println("\nPDF Location: " + pdfFile.getAbsolutePath());
                System.out.println("\nOptions to share PDF via WhatsApp:");
                System.out.println("1. Manually: Open WhatsApp and attach the PDF from " + PDF_OUTPUT_PATH);
                System.out.println("2. Email: Run send-database-email.bat to send via email");
                System.out.println("3. Upload to Google Drive/Dropbox and share link\n");
                return;
            }
            
            System.out.println("✓ PDF uploaded successfully!");
            System.out.println("✓ Public URL: " + pdfUrl);
            System.out.println("✓ Valid for: 14 days (auto-delete after)");
            
            // Step 3: Send PDF link via WhatsApp (text with link, not media attachment)
            System.out.println("\nStep 3: Sending PDF link via WhatsApp...\n");
            String message = generatePDFWhatsAppMessage(fileSize);
            
            // Try sending as media first, fallback to text link
            boolean success = false;
            System.out.println("Attempting to send as media attachment...");
            success = whatsAppService.sendPDF(message, PDF_OUTPUT_PATH, pdfUrl);
            
            if (!success) {
                System.out.println("\n⚠ Media attachment failed. Sending as text link instead...\n");
                success = whatsAppService.sendPDFLink(message, pdfUrl);
            }
            
            if (!success) {
                System.err.println("\nFailed to send PDF via WhatsApp.");
                System.err.println("But you can access it at: " + pdfUrl);
            } else {
                System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("║            PDF SENT VIA WHATSAPP SUCCESSFULLY!                     ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════╝");
                System.out.println("\n✓ PDF sent to your WhatsApp: +918767599309");
                System.out.println("✓ Download link included in message");
                System.out.println("✓ Public link: " + pdfUrl);
                System.out.println("\nNote: Link expires in 14 days and deletes automatically.\n");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate message for WhatsApp with PDF attachment
     */
    private static String generatePDFWhatsAppMessage(String fileSize) {
        StringBuilder message = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        message.append("📊 *BANK DATABASE REPORT*\n\n");
        message.append("Your comprehensive database report is attached as PDF.\n\n");
        message.append("📄 File: database_report.pdf\n");
        message.append("📦 Size: ").append(fileSize).append("\n");
        message.append("🕐 Generated: ").append(dateFormat.format(new Date())).append("\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            int totalUsers = getCount(conn, "SELECT COUNT(*) FROM signup");
            int totalAccounts = getCount(conn, "SELECT COUNT(*) FROM account");
            int totalTransactions = getCount(conn, "SELECT COUNT(*) FROM transactions");
            long totalBalance = getTotalBalance(conn);
            
            message.append("*Report Contents:*\n");
            message.append("• Executive Summary\n");
            message.append("• User Information (").append(totalUsers).append(" users)\n");
            message.append("• Account Details (").append(totalAccounts).append(" accounts)\n");
            message.append("• Transactions (").append(totalTransactions).append(" records)\n");
            message.append("• Balance Summary (₹").append(String.format("%,d", totalBalance)).append(")\n");
            message.append("• Statistical Analysis\n\n");
            
        } catch (SQLException e) {
            message.append("\n_Error retrieving summary_\n\n");
        }
        
        message.append("⏰ Note: Download link expires in 14 days");
        
        return message.toString();
    }
    
    /**
     * Generate notification message about PDF report
     */
    private static String generatePDFNotificationMessage(String fileSize) {
        StringBuilder message = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        message.append("📄 *PDF REPORT GENERATED*\n\n");
        message.append("═══════════════════════\n");
        message.append("Your comprehensive database report has been generated.\n\n");
        message.append("📊 Report Details:\n");
        message.append("• Format: PDF\n");
        message.append("• Size: ").append(fileSize).append("\n");
        message.append("• Generated: ").append(dateFormat.format(new Date())).append("\n");
        message.append("═══════════════════════\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            int totalUsers = getCount(conn, "SELECT COUNT(*) FROM signup");
            int totalAccounts = getCount(conn, "SELECT COUNT(*) FROM account");
            int totalTransactions = getCount(conn, "SELECT COUNT(*) FROM transactions");
            long totalBalance = getTotalBalance(conn);
            
            message.append("*Quick Summary:*\n");
            message.append("👥 Users: ").append(totalUsers).append("\n");
            message.append("💳 Accounts: ").append(totalAccounts).append("\n");
            message.append("📝 Transactions: ").append(totalTransactions).append("\n");
            message.append("💰 Total Balance: ₹").append(String.format("%,d", totalBalance)).append("\n\n");
            
        } catch (SQLException e) {
            message.append("\n_Error retrieving summary data_\n\n");
        }
        
        message.append("📧 Full PDF report available via email.\n");
        message.append("📁 Local: reports/database_report.pdf");
        
        return message.toString();
    }
    
    /**
     * Format file size
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
    
    /**
     * Generate a concise summary report for WhatsApp
     * @return Formatted summary string
     */
    private static String generateReportSummary() {
        StringBuilder summary = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        summary.append("📊 *DATABASE REPORT SUMMARY*\n\n");
        summary.append("Generated: ").append(dateFormat.format(new Date())).append("\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Get summary statistics
            int totalUsers = getCount(conn, "SELECT COUNT(*) FROM signup");
            int totalAccounts = getCount(conn, "SELECT COUNT(*) FROM account");
            int totalTransactions = getCount(conn, "SELECT COUNT(*) FROM transactions");
            long totalBalance = getTotalBalance(conn);
            
            summary.append("*KEY METRICS*\n");
            summary.append("━━━━━━━━━━━━━━━━━━━━\n");
            summary.append("👥 Users: ").append(totalUsers).append("\n");
            summary.append("💳 Accounts: ").append(totalAccounts).append("\n");
            summary.append("📝 Transactions: ").append(totalTransactions).append("\n");
            summary.append("💰 Total Balance: ₹").append(String.format("%,d", totalBalance)).append("\n\n");
            
            // Recent transactions summary
            String recentTrans = getRecentTransactionsSummary(conn);
            if (recentTrans != null && !recentTrans.isEmpty()) {
                summary.append("*RECENT ACTIVITY*\n");
                summary.append("━━━━━━━━━━━━━━━━━━━━\n");
                summary.append(recentTrans);
            }
            
            // Top accounts by balance
            String topAccounts = getTopAccountsSummary(conn);
            if (topAccounts != null && !topAccounts.isEmpty()) {
                summary.append("\n*TOP ACCOUNTS*\n");
                summary.append("━━━━━━━━━━━━━━━━━━━━\n");
                summary.append(topAccounts);
            }
            
        } catch (SQLException e) {
            summary.append("\n❌ Error retrieving data: ").append(e.getMessage());
        }
        
        summary.append("\n_For detailed report, check email or PDF_");
        
        return summary.toString();
    }
    
    /**
     * Get recent transactions summary (last 5)
     */
    private static String getRecentTransactionsSummary(Connection conn) throws SQLException {
        StringBuilder summary = new StringBuilder();
        String query = "SELECT type, amount, date_ms FROM transactions ORDER BY id DESC LIMIT 5";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                long dateMs = rs.getLong("date_ms");
                
                Date date = new Date(dateMs);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
                
                String emoji = type.equalsIgnoreCase("Deposit") ? "💰" : "💸";
                summary.append(emoji).append(" ").append(type).append(" ₹")
                       .append(String.format("%,d", amount))
                       .append(" (").append(sdf.format(date)).append(")\n");
            }
            
            if (count == 0) {
                return "No recent transactions";
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Get top 3 accounts by balance
     */
    private static String getTopAccountsSummary(Connection conn) throws SQLException {
        StringBuilder summary = new StringBuilder();
        String query = "SELECT a.card_no, s.name, " +
                       "COALESCE(SUM(CASE WHEN t.type = 'Deposit' THEN t.amount ELSE -t.amount END), 0) as balance " +
                       "FROM account a " +
                       "JOIN signup s ON a.form_no = s.form_no " +
                       "LEFT JOIN transactions t ON a.pin = t.pin " +
                       "GROUP BY a.card_no, s.name " +
                       "ORDER BY balance DESC LIMIT 3";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                long balance = rs.getLong("balance");
                
                summary.append(count).append(". ").append(name)
                       .append(": ₹").append(String.format("%,d", balance)).append("\n");
            }
            
            if (count == 0) {
                return "No accounts found";
            }
        }
        
        return summary.toString();
    }
    
    private static int getCount(Connection conn, String query) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.getInt(1);
        }
    }
    
    private static long getTotalBalance(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END), 0) as total " +
                       "FROM transactions";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.getLong("total");
        }
    }
}
