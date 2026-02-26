package bank.management.system;

import java.sql.*;

public class DatabaseViewer {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                System.out.println("╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("║          BANK MANAGEMENT SYSTEM - DATABASE VIEWER                  ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
                
                // View Signup Table
                viewSignupTable(conn);
                
                // View Signup Two Table
                viewSignupTwoTable(conn);
                
                // View Account Table
                viewAccountTable(conn);
                
                // View Transactions Table
                viewTransactionsTable(conn);
                
                // View Account Balances
                viewBalances(conn);
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: SQLite JDBC driver not found - " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }
    
    private static void viewSignupTable(Connection conn) throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                          SIGNUP TABLE");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        
        String query = "SELECT * FROM signup";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                System.out.println("\nForm No    : " + rs.getString("form_no"));
                System.out.println("Name       : " + rs.getString("name"));
                System.out.println("Father Name: " + rs.getString("fname"));
                System.out.println("DOB        : " + rs.getString("dob"));
                System.out.println("Gender     : " + rs.getString("gender"));
                System.out.println("Email      : " + rs.getString("email"));
                System.out.println("Marital    : " + rs.getString("marital"));
                System.out.println("Address    : " + rs.getString("address"));
                System.out.println("City       : " + rs.getString("city"));
                System.out.println("Pin Code   : " + rs.getString("pin_code"));
                System.out.println("State      : " + rs.getString("state"));
                System.out.println("-------------------------------------------------------------------");
            }
        }
    }
    
    private static void viewSignupTwoTable(Connection conn) throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                       SIGNUP TWO TABLE");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        
        String query = "SELECT * FROM signup_two";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                System.out.println("\nForm No         : " + rs.getString("form_no"));
                System.out.println("Religion        : " + rs.getString("religion"));
                System.out.println("Category        : " + rs.getString("category"));
                System.out.println("Income          : " + rs.getString("income"));
                System.out.println("Education       : " + rs.getString("education"));
                System.out.println("Occupation      : " + rs.getString("occupation"));
                System.out.println("PAN             : " + rs.getString("pan"));
                System.out.println("Aadhaar         : " + rs.getString("aadhaar"));
                System.out.println("Senior Citizen  : " + rs.getString("senior_citizen"));
                System.out.println("Existing Account: " + rs.getString("existing_account"));
                System.out.println("-------------------------------------------------------------------");
            }
        }
    }
    
    private static void viewAccountTable(Connection conn) throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                         ACCOUNT TABLE");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        
        String query = "SELECT * FROM account";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                System.out.println("\nForm No     : " + rs.getString("form_no"));
                System.out.println("Account Type: " + rs.getString("account_type"));
                System.out.println("Services    : " + rs.getString("services"));
                System.out.println("Card No     : " + rs.getString("card_no"));
                System.out.println("PIN         : " + rs.getString("pin"));
                System.out.println("Created At  : " + rs.getString("created_at"));
                System.out.println("-------------------------------------------------------------------");
            }
        }
    }
    
    private static void viewTransactionsTable(Connection conn) throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                       TRANSACTIONS TABLE");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        
        String query = "SELECT * FROM transactions ORDER BY date_ms DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            System.out.printf("\n%-5s %-20s %-25s %-15s %10s\n", "ID", "PIN", "Date", "Type", "Amount");
            System.out.println("-------------------------------------------------------------------");
            
            while (rs.next()) {
                long dateMs = rs.getLong("date_ms");
                java.util.Date date = new java.util.Date(dateMs);
                System.out.printf("%-5d %-20s %-25s %-15s %10d\n",
                    rs.getInt("id"),
                    rs.getString("pin"),
                    date.toString(),
                    rs.getString("type"),
                    rs.getInt("amount")
                );
            }
        }
    }
    
    private static void viewBalances(Connection conn) throws SQLException {
        System.out.println("\n═══════════════════════════════════════════════════════════════════");
        System.out.println("                        ACCOUNT BALANCES");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        
        String query = """
            SELECT 
                a.card_no,
                a.pin,
                s.name,
                COALESCE(SUM(CASE WHEN t.type = 'Deposit' THEN t.amount ELSE 0 END), 0) -
                COALESCE(SUM(CASE WHEN t.type = 'Withdrawl' THEN t.amount ELSE 0 END), 0) as balance
            FROM account a
            LEFT JOIN signup s ON a.form_no = s.form_no
            LEFT JOIN transactions t ON a.pin = t.pin
            GROUP BY a.card_no, a.pin, s.name
        """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            System.out.printf("\n%-20s %-20s %-20s %15s\n", "Card Number", "PIN", "Name", "Balance");
            System.out.println("-------------------------------------------------------------------");
            
            while (rs.next()) {
                System.out.printf("%-20s %-20s %-20s ₹%,14d\n",
                    rs.getString("card_no"),
                    rs.getString("pin"),
                    rs.getString("name"),
                    rs.getInt("balance")
                );
            }
            
            System.out.println("═══════════════════════════════════════════════════════════════════\n");
        }
    }
}
