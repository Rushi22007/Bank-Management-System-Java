package bank.management.system;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionMonitor {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static final long CHECK_INTERVAL = 5000; // Check every 5 seconds
    private static final long EMAIL_DELAY = 60000; // 1 minute delay before sending email
    
    private static Set<Integer> processedTransactions = new HashSet<>();
    private static int lastTransactionId = 0;
    private static Timer checkTimer;
    private static int emailsSent = 0;
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║    BANK MANAGEMENT SYSTEM - TRANSACTION MONITOR                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            System.out.println("📧 Sender: officialshreeman@gmail.com");
            System.out.println("📥 Recipient: rushikeshchamale5@gmail.com");
            System.out.println("⏱️  Email Delay: 1 minute after transaction");
            System.out.println("🔄 Status: Starting transaction monitor...\n");
            System.out.println("Press Ctrl+C to stop the monitor\n");
            System.out.println("═══════════════════════════════════════════════════════════════════\n");
            
            // Initialize - get the latest transaction ID
            initializeLastTransactionId();
            
            // Start monitoring for new transactions
            checkTimer = new Timer("TransactionMonitor", true);
            checkTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    checkForNewTransactions();
                }
            }, 0, CHECK_INTERVAL);
            
            System.out.println("✅ Transaction monitor is active.");
            System.out.println("📊 Monitoring database for new transactions...");
            System.out.println("📧 Will send email 1 minute after each transaction\n");
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("\n\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║              MONITOR STOPPED BY USER                               ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nTotal emails sent: " + emailsSent);
            if (checkTimer != null) {
                checkTimer.cancel();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize by getting the latest transaction ID from database
     */
    private static void initializeLastTransactionId() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT MAX(id) as max_id FROM transactions";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    lastTransactionId = rs.getInt("max_id");
                    System.out.println("🔍 Initialized. Last transaction ID: " + lastTransactionId);
                    System.out.println("📌 Watching for transactions after ID " + lastTransactionId + "\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing: " + e.getMessage());
        }
    }
    
    /**
     * Check for new transactions in the database
     */
    private static void checkForNewTransactions() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT t.*, a.card_no, s.name " +
                          "FROM transactions t " +
                          "JOIN account a ON t.pin = a.pin " +
                          "JOIN signup s ON a.form_no = s.form_no " +
                          "WHERE t.id > ? " +
                          "ORDER BY t.id ASC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, lastTransactionId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int transId = rs.getInt("id");
                        String name = rs.getString("name");
                        String cardNo = rs.getString("card_no");
                        String type = rs.getString("type");
                        int amount = rs.getInt("amount");
                        long dateMs = rs.getLong("date_ms");
                        String pin = rs.getString("pin");
                        
                        // Update last transaction ID
                        if (transId > lastTransactionId) {
                            lastTransactionId = transId;
                        }
                        
                        // Check if already processed
                        if (!processedTransactions.contains(transId)) {
                            processedTransactions.add(transId);
                            onNewTransaction(transId, name, cardNo, type, amount, dateMs, pin);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking transactions: " + e.getMessage());
        }
    }
    
    /**
     * Called when a new transaction is detected
     */
    private static void onNewTransaction(int transId, String name, String cardNo, 
                                        String type, int amount, long dateMs, String pin) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String transTime = dateFormat.format(new Date(dateMs));
        
        String icon = type.equalsIgnoreCase("Deposit") ? "💰" : "💸";
        
        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("🆕 NEW TRANSACTION DETECTED!");
        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("Transaction ID : " + transId);
        System.out.println("Type           : " + icon + " " + type.toUpperCase());
        System.out.println("Customer       : " + name);
        System.out.println("Card Number    : " + cardNo);
        System.out.println("Amount         : ₹" + String.format("%,d", amount));
        System.out.println("Transaction At : " + transTime);
        System.out.println("Detected At    : " + currentTime);
        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("⏳ Scheduling email to be sent in 1 minute...\n");
        
        // Schedule email to be sent after 1 minute
        Timer emailTimer = new Timer("EmailSender-" + transId, true);
        emailTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTransactionEmail(transId, name, cardNo, type, amount, dateMs, pin);
                emailTimer.cancel();
            }
        }, EMAIL_DELAY);
    }
    
    /**
     * Send email report for the transaction
     */
    private static void sendTransactionEmail(int transId, String name, String cardNo, 
                                            String type, int amount, long dateMs, String pin) {
        emailsSent++;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());
        
        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("📨 Sending Email #" + emailsSent + " | Time: " + currentTime);
        System.out.println("📌 For Transaction ID: " + transId);
        System.out.println("─────────────────────────────────────────────────────────────────────");
        
        try {
            String report = generateTransactionReport(transId, name, cardNo, type, amount, dateMs, pin);
            EmailService emailService = new EmailService();
            boolean success = emailService.sendEmail(report);
            
            if (success) {
                System.out.println("✅ Transaction report sent successfully!");
                System.out.println("📧 Email sent to rushikeshchamale5@gmail.com");
            } else {
                System.out.println("❌ Failed to send email. Check configuration.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error sending report: " + e.getMessage());
        }
        
        System.out.println("🔍 Continuing to monitor for new transactions...\n");
    }
    
    /**
     * Generate detailed transaction report
     */
    private static String generateTransactionReport(int transId, String name, String cardNo, 
                                                    String type, int amount, long dateMs, String pin) {
        StringBuilder report = new StringBuilder();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        String transDateTime = dateFormat.format(new Date(dateMs));
        
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("      BANK MANAGEMENT SYSTEM - TRANSACTION ALERT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("Report Generated: ").append(currentDateTime).append("\n");
        report.append("Email Number: #").append(emailsSent).append("\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        // Transaction details
        String icon = type.equalsIgnoreCase("Deposit") ? "DEPOSIT" : "WITHDRAWAL";
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                    TRANSACTION DETAILS\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");
        report.append("Transaction ID    : ").append(transId).append("\n");
        report.append("Transaction Type  : ").append(icon).append("\n");
        report.append("Amount            : ₹").append(String.format("%,d", amount)).append("\n");
        report.append("Transaction Time  : ").append(transDateTime).append("\n\n");
        
        report.append("Customer Name     : ").append(name).append("\n");
        report.append("Card Number       : ").append(cardNo).append("\n");
        report.append("PIN               : ").append(pin).append("\n");
        
        // Get current balance
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String balanceQuery = "SELECT " +
                                 "COALESCE(SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END), 0) as balance " +
                                 "FROM transactions WHERE pin = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(balanceQuery)) {
                pstmt.setString(1, pin);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        long balance = rs.getLong("balance");
                        report.append("\n-------------------------------------------------------------------\n");
                        report.append("Current Balance   : ₹").append(String.format("%,d", balance)).append("\n");
                    }
                }
            }
            
            // Get recent transactions for this account
            report.append("\n\n═══════════════════════════════════════════════════════════════════\n");
            report.append("              RECENT TRANSACTIONS (Last 5)\n");
            report.append("═══════════════════════════════════════════════════════════════════\n\n");
            
            String recentQuery = "SELECT * FROM transactions WHERE pin = ? ORDER BY date_ms DESC LIMIT 5";
            try (PreparedStatement pstmt = conn.prepareStatement(recentQuery)) {
                pstmt.setString(1, pin);
                try (ResultSet rs = pstmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        int id = rs.getInt("id");
                        String t = rs.getString("type");
                        int amt = rs.getInt("amount");
                        long ms = rs.getLong("date_ms");
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        report.append(String.format("%d. %s - ₹%,d\n", count, t, amt));
                        report.append(String.format("   Date: %s | ID: %d\n\n", sdf.format(new Date(ms)), id));
                    }
                }
            }
            
            // Get account summary
            report.append("═══════════════════════════════════════════════════════════════════\n");
            report.append("                    ACCOUNT SUMMARY\n");
            report.append("═══════════════════════════════════════════════════════════════════\n\n");
            
            String summaryQuery = "SELECT " +
                                 "COUNT(*) as total_trans, " +
                                 "SUM(CASE WHEN type = 'Deposit' THEN amount ELSE 0 END) as total_deposits, " +
                                 "SUM(CASE WHEN type = 'Withdrawl' THEN amount ELSE 0 END) as total_withdrawals " +
                                 "FROM transactions WHERE pin = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(summaryQuery)) {
                pstmt.setString(1, pin);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int totalTrans = rs.getInt("total_trans");
                        long totalDeposits = rs.getLong("total_deposits");
                        long totalWithdrawals = rs.getLong("total_withdrawals");
                        
                        report.append("Total Transactions: ").append(totalTrans).append("\n");
                        report.append("Total Deposits    : ₹").append(String.format("%,d", totalDeposits)).append("\n");
                        report.append("Total Withdrawals : ₹").append(String.format("%,d", totalWithdrawals)).append("\n");
                    }
                }
            }
            
        } catch (SQLException e) {
            report.append("\n\nError retrieving account details: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n═══════════════════════════════════════════════════════════════════\n");
        report.append("                END OF TRANSACTION ALERT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("\nThis is an automated alert sent 1 minute after transaction.\n");
        report.append("Sender: officialshreeman@gmail.com\n");
        report.append("Recipient: rushikeshchamale5@gmail.com\n");
        
        return report.toString();
    }
}
