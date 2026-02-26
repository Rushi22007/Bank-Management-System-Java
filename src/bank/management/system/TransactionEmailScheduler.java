package bank.management.system;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionEmailScheduler {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static final long INTERVAL_MS = 60000; // 1 minute = 60,000 milliseconds
    private static int emailCount = 0;
    private static Timer timer;
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║    BANK MANAGEMENT SYSTEM - AUTO TRANSACTION EMAIL REPORTER       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            System.out.println("📧 Sender: officialshreeman@gmail.com");
            System.out.println("⏱️  Interval: Every 1 minute");
            System.out.println("🔄 Status: Starting automatic email scheduler...\n");
            System.out.println("Press Ctrl+C to stop the scheduler\n");
            System.out.println("═══════════════════════════════════════════════════════════════════\n");
            
            // Send first report immediately
            sendTransactionReport();
            
            // Schedule to run every 1 minute
            timer = new Timer("TransactionEmailScheduler", true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendTransactionReport();
                }
            }, INTERVAL_MS, INTERVAL_MS);
            
            // Keep the program running
            System.out.println("Scheduler is running. Waiting for next scheduled email...");
            System.out.println("(Press Ctrl+C to stop)\n");
            
            // Keep main thread alive
            Thread.currentThread().join();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("\n\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║              SCHEDULER STOPPED BY USER                             ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nTotal reports sent: " + emailCount);
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send transaction report via email
     */
    private static void sendTransactionReport() {
        emailCount++;
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new Date());
        
        System.out.println("─────────────────────────────────────────────────────────────────────");
        System.out.println("📨 Email #" + emailCount + " | Time: " + currentTime);
        System.out.println("─────────────────────────────────────────────────────────────────────");
        
        try {
            String report = generateTransactionReport();
            EmailService emailService = new EmailService();
            boolean success = emailService.sendEmail(report);
            
            if (success) {
                System.out.println("✅ Transaction report sent successfully!");
            } else {
                System.out.println("❌ Failed to send email. Check configuration.");
                System.out.println("   Make sure to set your App Password in email.properties");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error generating/sending report: " + e.getMessage());
        }
        
        System.out.println("⏳ Next email in 1 minute...\n");
    }
    
    /**
     * Generate transaction report with current statistics
     */
    private static String generateTransactionReport() {
        StringBuilder report = new StringBuilder();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("      BANK MANAGEMENT SYSTEM - TRANSACTION REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("Report Generated: ").append(currentDateTime).append("\n");
        report.append("Report Number: #").append(emailCount).append("\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            
            // Get transaction summary
            report.append(getTransactionSummary(conn));
            report.append("\n\n");
            
            // Get recent transactions (last 10)
            report.append(getRecentTransactions(conn));
            report.append("\n\n");
            
            // Get current account balances
            report.append(getAccountBalances(conn));
            report.append("\n\n");
            
            // Get today's activity
            report.append(getTodayActivity(conn));
            
        } catch (SQLException e) {
            report.append("\n\nERROR: Unable to retrieve transaction data\n");
            report.append("Error message: ").append(e.getMessage()).append("\n");
        }
        
        report.append("\n═══════════════════════════════════════════════════════════════════\n");
        report.append("              END OF TRANSACTION REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("\nThis is an automated report sent every 1 minute.\n");
        report.append("Sender: officialshreeman@gmail.com\n");
        
        return report.toString();
    }
    
    private static String getTransactionSummary(Connection conn) throws SQLException {
        StringBuilder summary = new StringBuilder();
        summary.append("═══════════════════════════════════════════════════════════════════\n");
        summary.append("                    TRANSACTION SUMMARY\n");
        summary.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        String query = "SELECT " +
                      "COUNT(*) as total_transactions, " +
                      "SUM(CASE WHEN type = 'Deposit' THEN amount ELSE 0 END) as total_deposits, " +
                      "SUM(CASE WHEN type = 'Withdrawl' THEN amount ELSE 0 END) as total_withdrawals, " +
                      "SUM(CASE WHEN type = 'Deposit' THEN 1 ELSE 0 END) as deposit_count, " +
                      "SUM(CASE WHEN type = 'Withdrawl' THEN 1 ELSE 0 END) as withdrawal_count " +
                      "FROM transactions";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int totalTrans = rs.getInt("total_transactions");
                long totalDeposits = rs.getLong("total_deposits");
                long totalWithdrawals = rs.getLong("total_withdrawals");
                int depositCount = rs.getInt("deposit_count");
                int withdrawalCount = rs.getInt("withdrawal_count");
                
                summary.append("Total Transactions    : ").append(totalTrans).append("\n");
                summary.append("  • Deposits          : ").append(depositCount).append(" (₹").append(String.format("%,d", totalDeposits)).append(")\n");
                summary.append("  • Withdrawals       : ").append(withdrawalCount).append(" (₹").append(String.format("%,d", totalWithdrawals)).append(")\n");
                summary.append("\nNet Flow              : ₹").append(String.format("%,d", (totalDeposits - totalWithdrawals))).append("\n");
            }
        }
        
        return summary.toString();
    }
    
    private static String getRecentTransactions(Connection conn) throws SQLException {
        StringBuilder recent = new StringBuilder();
        recent.append("═══════════════════════════════════════════════════════════════════\n");
        recent.append("                  RECENT TRANSACTIONS (Last 10)\n");
        recent.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        String query = "SELECT t.*, a.card_no, s.name " +
                      "FROM transactions t " +
                      "JOIN account a ON t.pin = a.pin " +
                      "JOIN signup s ON a.form_no = s.form_no " +
                      "ORDER BY t.date_ms DESC LIMIT 10";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String cardNo = rs.getString("card_no");
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                long dateMs = rs.getLong("date_ms");
                
                Date date = new Date(dateMs);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                String icon = type.equalsIgnoreCase("Deposit") ? "💰" : "💸";
                
                recent.append(String.format("%d. %s %s\n", count, icon, type.toUpperCase()));
                recent.append(String.format("   Name    : %s\n", name));
                recent.append(String.format("   Card    : %s\n", cardNo));
                recent.append(String.format("   Amount  : ₹%,d\n", amount));
                recent.append(String.format("   Date    : %s\n", sdf.format(date)));
                recent.append("   -------------------------------------------------------------------\n");
            }
            
            if (count == 0) {
                recent.append("No transactions found.\n");
            }
        }
        
        return recent.toString();
    }
    
    private static String getAccountBalances(Connection conn) throws SQLException {
        StringBuilder balances = new StringBuilder();
        balances.append("═══════════════════════════════════════════════════════════════════\n");
        balances.append("                    CURRENT ACCOUNT BALANCES\n");
        balances.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        String query = "SELECT a.card_no, a.pin, s.name, " +
                      "COALESCE(SUM(CASE WHEN t.type = 'Deposit' THEN t.amount ELSE -t.amount END), 0) as balance " +
                      "FROM account a " +
                      "JOIN signup s ON a.form_no = s.form_no " +
                      "LEFT JOIN transactions t ON a.pin = t.pin " +
                      "GROUP BY a.card_no, a.pin, s.name " +
                      "ORDER BY balance DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            long totalBalance = 0;
            
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String cardNo = rs.getString("card_no");
                long balance = rs.getLong("balance");
                totalBalance += balance;
                
                balances.append(String.format("%d. %-25s : ₹%,15d\n", count, name, balance));
                balances.append(String.format("   Card: %s\n\n", cardNo));
            }
            
            balances.append("-------------------------------------------------------------------\n");
            balances.append(String.format("Total System Balance      : ₹%,d\n", totalBalance));
            balances.append(String.format("Active Accounts           : %d\n", count));
        }
        
        return balances.toString();
    }
    
    private static String getTodayActivity(Connection conn) throws SQLException {
        StringBuilder today = new StringBuilder();
        today.append("═══════════════════════════════════════════════════════════════════\n");
        today.append("                      TODAY'S ACTIVITY\n");
        today.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        // Get today's start timestamp (midnight)
        long todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000);
        
        String query = "SELECT " +
                      "COUNT(*) as today_transactions, " +
                      "SUM(CASE WHEN type = 'Deposit' THEN amount ELSE 0 END) as today_deposits, " +
                      "SUM(CASE WHEN type = 'Withdrawl' THEN amount ELSE 0 END) as today_withdrawals " +
                      "FROM transactions " +
                      "WHERE date_ms >= ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, todayStart);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int todayTrans = rs.getInt("today_transactions");
                    long todayDeposits = rs.getLong("today_deposits");
                    long todayWithdrawals = rs.getLong("today_withdrawals");
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                    today.append("Date: ").append(dateFormat.format(new Date())).append("\n\n");
                    today.append("Transactions Today    : ").append(todayTrans).append("\n");
                    today.append("Deposits Today        : ₹").append(String.format("%,d", todayDeposits)).append("\n");
                    today.append("Withdrawals Today     : ₹").append(String.format("%,d", todayWithdrawals)).append("\n");
                    today.append("Net Today             : ₹").append(String.format("%,d", (todayDeposits - todayWithdrawals))).append("\n");
                }
            }
        }
        
        return today.toString();
    }
}
