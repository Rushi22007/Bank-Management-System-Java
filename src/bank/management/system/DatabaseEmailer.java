package bank.management.system;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseEmailer {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║       BANK MANAGEMENT SYSTEM - DATABASE EMAIL SENDER              ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            System.out.println("Gathering database information...\n");
            
            // Generate database report
            String report = generateDatabaseReport();
            
            // Send email
            EmailService emailService = new EmailService();
            boolean success = emailService.sendEmail(report);
            
            if (!success) {
                System.err.println("\nThe database report was generated but could not be sent via email.");
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
     * Generate a comprehensive database report as a formatted string
     * @return Formatted database report
     */
    private static String generateDatabaseReport() {
        StringBuilder report = new StringBuilder();
        
        // Add header
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("         BANK MANAGEMENT SYSTEM - DATABASE REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("Generated: ").append(currentDateTime).append("\n");
        report.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Add all section reports
            report.append(getSignupTableReport(conn));
            report.append("\n\n");
            report.append(getSignupTwoTableReport(conn));
            report.append("\n\n");
            report.append(getAccountTableReport(conn));
            report.append("\n\n");
            report.append(getTransactionsReport(conn));
            report.append("\n\n");
            report.append(getBalancesReport(conn));
            
        } catch (SQLException e) {
            report.append("\n\nERROR: Unable to retrieve database information\n");
            report.append("Error message: ").append(e.getMessage()).append("\n");
        }
        
        // Add footer
        report.append("\n═══════════════════════════════════════════════════════════════════\n");
        report.append("                    END OF REPORT\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        return report.toString();
    }
    
    private static String getSignupTableReport(Connection conn) throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                       REGISTERED USERS\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        String query = "SELECT * FROM signup";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                report.append("\n[USER #").append(count).append("]\n");
                report.append("Form No    : ").append(rs.getString("form_no")).append("\n");
                report.append("Name       : ").append(rs.getString("name")).append("\n");
                report.append("Father Name: ").append(rs.getString("fname")).append("\n");
                report.append("DOB        : ").append(rs.getString("dob")).append("\n");
                report.append("Gender     : ").append(rs.getString("gender")).append("\n");
                report.append("Email      : ").append(rs.getString("email")).append("\n");
                report.append("Marital    : ").append(rs.getString("marital")).append("\n");
                report.append("Address    : ").append(rs.getString("address")).append("\n");
                report.append("City       : ").append(rs.getString("city")).append("\n");
                report.append("Pin Code   : ").append(rs.getString("pin_code")).append("\n");
                report.append("State      : ").append(rs.getString("state")).append("\n");
                report.append("-------------------------------------------------------------------\n");
            }
            
            if (count == 0) {
                report.append("\nNo registered users found.\n");
            } else {
                report.append("\nTotal Users: ").append(count).append("\n");
            }
        }
        
        return report.toString();
    }
    
    private static String getSignupTwoTableReport(Connection conn) throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                   ADDITIONAL USER DETAILS\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        String query = "SELECT * FROM signup_two";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                report.append("\n[DETAILS #").append(count).append("]\n");
                report.append("Form No         : ").append(rs.getString("form_no")).append("\n");
                report.append("Religion        : ").append(rs.getString("religion")).append("\n");
                report.append("Category        : ").append(rs.getString("category")).append("\n");
                report.append("Income          : ").append(rs.getString("income")).append("\n");
                report.append("Education       : ").append(rs.getString("education")).append("\n");
                report.append("Occupation      : ").append(rs.getString("occupation")).append("\n");
                report.append("PAN             : ").append(rs.getString("pan")).append("\n");
                report.append("Aadhaar         : ").append(rs.getString("aadhaar")).append("\n");
                report.append("Senior Citizen  : ").append(rs.getString("senior_citizen")).append("\n");
                report.append("Existing Account: ").append(rs.getString("existing_account")).append("\n");
                report.append("-------------------------------------------------------------------\n");
            }
            
            if (count == 0) {
                report.append("\nNo additional details found.\n");
            }
        }
        
        return report.toString();
    }
    
    private static String getAccountTableReport(Connection conn) throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                         ACTIVE ACCOUNTS\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        String query = "SELECT * FROM account";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                report.append("\n[ACCOUNT #").append(count).append("]\n");
                report.append("Form No     : ").append(rs.getString("form_no")).append("\n");
                report.append("Account Type: ").append(rs.getString("account_type")).append("\n");
                report.append("Services    : ").append(rs.getString("services")).append("\n");
                report.append("Card No     : ").append(rs.getString("card_no")).append("\n");
                report.append("PIN         : ").append(rs.getString("pin")).append("\n");
                report.append("Created At  : ").append(rs.getString("created_at")).append("\n");
                report.append("-------------------------------------------------------------------\n");
            }
            
            if (count == 0) {
                report.append("\nNo active accounts found.\n");
            } else {
                report.append("\nTotal Accounts: ").append(count).append("\n");
            }
        }
        
        return report.toString();
    }
    
    private static String getTransactionsReport(Connection conn) throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                       TRANSACTION HISTORY\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        String query = "SELECT * FROM transactions ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            report.append(String.format("\n%-6s %-10s %-28s %-12s %12s\n", 
                "ID", "PIN", "Date", "Type", "Amount"));
            report.append("-------------------------------------------------------------------\n");
            
            int count = 0;
            long totalDeposits = 0;
            long totalWithdrawals = 0;
            
            while (rs.next()) {
                count++;
                int id = rs.getInt("id");
                String pin = rs.getString("pin");
                long dateMs = rs.getLong("date_ms");
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                
                Date date = new Date(dateMs);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                report.append(String.format("%-6d %-10s %-28s %-12s ₹%,11d\n", 
                    id, pin, sdf.format(date), type, amount));
                
                if (type.equalsIgnoreCase("Deposit")) {
                    totalDeposits += amount;
                } else if (type.equalsIgnoreCase("Withdrawl")) {
                    totalWithdrawals += amount;
                }
            }
            
            if (count == 0) {
                report.append("No transactions found.\n");
            } else {
                report.append("-------------------------------------------------------------------\n");
                report.append(String.format("Total Transactions: %d\n", count));
                report.append(String.format("Total Deposits    : ₹%,d\n", totalDeposits));
                report.append(String.format("Total Withdrawals : ₹%,d\n", totalWithdrawals));
            }
        }
        
        return report.toString();
    }
    
    private static String getBalancesReport(Connection conn) throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════════════════════\n");
        report.append("                        ACCOUNT BALANCES\n");
        report.append("═══════════════════════════════════════════════════════════════════\n");
        
        String query = "SELECT a.card_no, a.pin, s.name, " +
                       "COALESCE(SUM(CASE WHEN t.type = 'Deposit' THEN t.amount ELSE -t.amount END), 0) as balance " +
                       "FROM account a " +
                       "JOIN signup s ON a.form_no = s.form_no " +
                       "LEFT JOIN transactions t ON a.pin = t.pin " +
                       "GROUP BY a.card_no, a.pin, s.name " +
                       "ORDER BY balance DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            report.append(String.format("\n%-20s %-10s %-25s %15s\n", 
                "Card Number", "PIN", "Name", "Balance"));
            report.append("-------------------------------------------------------------------\n");
            
            int count = 0;
            long totalBalance = 0;
            
            while (rs.next()) {
                count++;
                String cardNo = rs.getString("card_no");
                String pin = rs.getString("pin");
                String name = rs.getString("name");
                long balance = rs.getLong("balance");
                
                totalBalance += balance;
                
                report.append(String.format("%-20s %-10s %-25s ₹%,14d\n", 
                    cardNo, pin, name, balance));
            }
            
            if (count == 0) {
                report.append("No accounts found.\n");
            } else {
                report.append("-------------------------------------------------------------------\n");
                report.append(String.format("Total Accounts: %d\n", count));
                report.append(String.format("Total System Balance: ₹%,d\n", totalBalance));
            }
        }
        
        return report.toString();
    }
}
