package bank.management.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseInitializer {
    private static final String DB_PATH = "db/bank.db";
    private static final String SCHEMA_PATH = "db/schema.sql";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public static void main(String[] args) {
        System.out.println("=== Bank Management System - Database Initializer ===\n");
        
        try {
            // Create db directory if it doesn't exist
            Files.createDirectories(Path.of("db"));
            System.out.println("✓ Database directory created/verified");
            
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            System.out.println("✓ SQLite JDBC driver loaded");
            
            // Read schema.sql file
            String schema = Files.readString(Path.of(SCHEMA_PATH));
            System.out.println("✓ Schema file read successfully");
            
            // Execute schema
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                
                // Split and execute SQL statements
                String[] statements = schema.split(";");
                int executed = 0;
                
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        stmt.execute(sql);
                        executed++;
                    }
                }
                
                System.out.println("✓ Executed " + executed + " SQL statements");
                
                // Verify tables were created
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, null, new String[]{"TABLE"});
                
                System.out.println("\n=== Database Tables Created ===");
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    
                    // Count rows in each table
                    String countQuery = "SELECT COUNT(*) as count FROM " + tableName;
                    try (Statement countStmt = conn.createStatement();
                         ResultSet rs = countStmt.executeQuery(countQuery)) {
                        if (rs.next()) {
                            int count = rs.getInt("count");
                            System.out.println("  • " + tableName + " (" + count + " rows)");
                        }
                    }
                }
                
                // Display demo account info
                System.out.println("\n=== Demo Account Created ===");
                String query = "SELECT card_no, pin FROM account WHERE card_no = '1234567890123456'";
                try (Statement demoStmt = conn.createStatement();
                     ResultSet rs = demoStmt.executeQuery(query)) {
                    if (rs.next()) {
                        System.out.println("  Card Number: " + rs.getString("card_no"));
                        System.out.println("  PIN: " + rs.getString("pin"));
                        System.out.println("  (Use these credentials to login)");
                    }
                }
                
                System.out.println("\n✓ Database initialized successfully!");
                System.out.println("    Location: " + Path.of(DB_PATH).toAbsolutePath());
                
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: SQLite JDBC driver not found");
            System.err.println("  Make sure sqlite-jdbc.jar is in the lib folder");
        } catch (IOException e) {
            System.err.println("✗ Error reading schema file: " + SCHEMA_PATH);
        } catch (SQLException e) {
            System.err.println("✗ Database error occurred: " + e.getMessage());
        }
    }
}
