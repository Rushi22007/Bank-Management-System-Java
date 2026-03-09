package bank.management.system;

import java.sql.*;
import java.util.Scanner;

/**
 * WhatsApp Interactive Service
 * Provides balance check and PIN change via WhatsApp notifications
 */
public class WhatsAppInteractive {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    private static WhatsAppService whatsAppService;
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            whatsAppService = new WhatsAppService();
            
            if (!whatsAppService.isEnabled()) {
                System.err.println("WhatsApp service is not configured properly.");
                return;
            }
            
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║        BANK MANAGEMENT SYSTEM - WHATSAPP SERVICES                 ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");
            
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                System.out.println("\nWhatsApp Services:");
                System.out.println("1. Send Balance Inquiry");
                System.out.println("2. Send PIN Change Confirmation");
                System.out.println("3. Send Transaction Alert (Manual)");
                System.out.println("4. Exit");
                System.out.print("\nEnter your choice: ");
                
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        handleBalanceInquiry(scanner);
                        break;
                    case "2":
                        handlePINChangeConfirmation(scanner);
                        break;
                    case "3":
                        handleManualTransactionAlert(scanner);
                        break;
                    case "4":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send balance inquiry via WhatsApp
     */
    private static void handleBalanceInquiry(Scanner scanner) {
        System.out.print("\nEnter PIN: ");
        String pin = scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Verify PIN exists
            if (!verifyPIN(conn, pin)) {
                System.err.println("Invalid PIN. Account not found.");
                return;
            }
            
            // Get balance
            int balance = getBalance(conn, pin);
            
            // Send WhatsApp message
            System.out.println("\nSending balance inquiry via WhatsApp...");
            boolean success = whatsAppService.sendBalanceInquiry(pin, balance);
            
            if (success) {
                System.out.println("✓ Balance inquiry sent successfully!");
                System.out.println("Current Balance: ₹" + String.format("%,d", balance));
            } else {
                System.err.println("Failed to send balance inquiry.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Send PIN change confirmation via WhatsApp
     */
    private static void handlePINChangeConfirmation(Scanner scanner) {
        System.out.print("\nEnter Card Number: ");
        String cardNumber = scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Verify card exists
            if (!verifyCard(conn, cardNumber)) {
                System.err.println("Invalid card number. Card not found.");
                return;
            }
            
            // Send WhatsApp message
            System.out.println("\nSending PIN change confirmation via WhatsApp...");
            boolean success = whatsAppService.sendPINChangeConfirmation(cardNumber);
            
            if (success) {
                System.out.println("✓ PIN change confirmation sent successfully!");
            } else {
                System.err.println("Failed to send PIN change confirmation.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Send manual transaction alert
     */
    private static void handleManualTransactionAlert(Scanner scanner) {
        System.out.print("\nEnter PIN: ");
        String pin = scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Verify PIN exists
            if (!verifyPIN(conn, pin)) {
                System.err.println("Invalid PIN. Account not found.");
                return;
            }
            
            System.out.print("Transaction Type (Deposit/Withdrawal): ");
            String type = scanner.nextLine();
            
            System.out.print("Amount: ");
            int amount = Integer.parseInt(scanner.nextLine());
            
            // Get current balance
            int balance = getBalance(conn, pin);
            
            // Send WhatsApp message
            System.out.println("\nSending transaction alert via WhatsApp...");
            boolean success = whatsAppService.sendTransactionAlert(pin, type, amount, balance);
            
            if (success) {
                System.out.println("✓ Transaction alert sent successfully!");
            } else {
                System.err.println("Failed to send transaction alert.");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount entered.");
        }
    }
    
    /**
     * Verify if PIN exists in database
     */
    private static boolean verifyPIN(Connection conn, String pin) throws SQLException {
        String query = "SELECT COUNT(*) FROM account WHERE pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, pin);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }
    
    /**
     * Verify if card number exists in database
     */
    private static boolean verifyCard(Connection conn, String cardNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM account WHERE card_no = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }
    
    /**
     * Get account balance
     */
    private static int getBalance(Connection conn, String pin) throws SQLException {
        String query = "SELECT COALESCE(SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END), 0) as balance " +
                       "FROM transactions WHERE pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, pin);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("balance");
        }
    }
    
    /**
     * Static method to send transaction alert from other classes
     */
    public static void sendTransactionAlert(String pin, String type, int amount) {
        try {
            Class.forName("org.sqlite.JDBC");
            WhatsAppService service = new WhatsAppService();
            
            if (service.isEnabled()) {
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    int balance = getBalance(conn, pin);
                    service.sendTransactionAlert(pin, type, amount, balance);
                }
            }
        } catch (Exception e) {
            // Silent fail - don't interrupt main transaction flow
            System.err.println("WhatsApp alert failed: " + e.getMessage());
        }
    }
}
