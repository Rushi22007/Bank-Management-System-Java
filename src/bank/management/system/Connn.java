package bank.management.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Connn {
    private static final String DB_PATH = "db/bank.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try {
            Files.createDirectories(Path.of("db"));
            Class.forName("org.sqlite.JDBC");
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to load SQLite driver or create db directory", e);
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys=ON");
                st.execute("""
                        CREATE TABLE IF NOT EXISTS signup (
                            form_no TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            fname TEXT NOT NULL,
                            dob TEXT NOT NULL,
                            gender TEXT NOT NULL,
                            email TEXT NOT NULL,
                            marital TEXT NOT NULL,
                            address TEXT NOT NULL,
                            city TEXT NOT NULL,
                            pin_code TEXT NOT NULL,
                            state TEXT NOT NULL
                        )
                        """);

                st.execute("""
                        CREATE TABLE IF NOT EXISTS signup_two (
                            form_no TEXT PRIMARY KEY,
                            religion TEXT NOT NULL,
                            category TEXT NOT NULL,
                            income TEXT NOT NULL,
                            education TEXT NOT NULL,
                            occupation TEXT NOT NULL,
                            pan TEXT NOT NULL,
                            aadhaar TEXT NOT NULL,
                            senior_citizen TEXT NOT NULL,
                            existing_account TEXT NOT NULL,
                            FOREIGN KEY(form_no) REFERENCES signup(form_no) ON DELETE CASCADE
                        )
                        """);

                st.execute("""
                        CREATE TABLE IF NOT EXISTS account (
                            form_no TEXT NOT NULL,
                            account_type TEXT NOT NULL,
                            services TEXT NOT NULL,
                            card_no TEXT PRIMARY KEY,
                            pin TEXT UNIQUE NOT NULL,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY(form_no) REFERENCES signup(form_no) ON DELETE CASCADE
                        )
                        """);

                st.execute("""
                        CREATE TABLE IF NOT EXISTS transactions (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            pin TEXT NOT NULL,
                            date_ms INTEGER NOT NULL,
                            type TEXT NOT NULL,
                            amount INTEGER NOT NULL,
                            FOREIGN KEY(pin) REFERENCES account(pin) ON DELETE CASCADE
                        )
                        """);
            }

            seedInitialData(conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize database schema", e);
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        // Keep the previous demo credentials available.
        String seedCard = "1234567890123456";
        String seedPin = "1234";
        String seedForm = "0001";

        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM account WHERE card_no = ? LIMIT 1")) {
            ps.setString(1, seedCard);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        try (PreparedStatement signup = conn.prepareStatement(
                "INSERT OR IGNORE INTO signup(form_no, name, fname, dob, gender, email, marital, address, city, pin_code, state) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement signupTwo = conn.prepareStatement(
                     "INSERT OR IGNORE INTO signup_two(form_no, religion, category, income, education, occupation, pan, aadhaar, senior_citizen, existing_account) " +
                             "VALUES (?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement account = conn.prepareStatement(
                     "INSERT INTO account(form_no, account_type, services, card_no, pin) VALUES (?,?,?,?,?)");
             PreparedStatement tx = conn.prepareStatement(
                     "INSERT INTO transactions(pin, date_ms, type, amount) VALUES (?,?,?,?)")) {

            signup.setString(1, seedForm);
            signup.setString(2, "Demo User");
            signup.setString(3, "Demo Father");
            signup.setString(4, "01/01/1990");
            signup.setString(5, "Other");
            signup.setString(6, "demo@example.com");
            signup.setString(7, "Other");
            signup.setString(8, "123 Demo Street");
            signup.setString(9, "Demo City");
            signup.setString(10, "000000");
            signup.setString(11, "Demo State");
            signup.executeUpdate();

            signupTwo.setString(1, seedForm);
            signupTwo.setString(2, "Other");
            signupTwo.setString(3, "Other");
            signupTwo.setString(4, "Null");
            signupTwo.setString(5, "Graduate");
            signupTwo.setString(6, "Salaried");
            signupTwo.setString(7, "AAAAA0000A");
            signupTwo.setString(8, "000000000000");
            signupTwo.setString(9, "No");
            signupTwo.setString(10, "No");
            signupTwo.executeUpdate();

            account.setString(1, seedForm);
            account.setString(2, "Saving Account");
            account.setString(3, "ATM CARD");
            account.setString(4, seedCard);
            account.setString(5, seedPin);
            account.executeUpdate();

            tx.setString(1, seedPin);
            tx.setLong(2, System.currentTimeMillis());
            tx.setString(3, "Deposit");
            tx.setInt(4, 10000);
            tx.executeUpdate();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void saveSignup(SignupRecord record) {
        String sql = "INSERT OR REPLACE INTO signup(form_no, name, fname, dob, gender, email, marital, address, city, pin_code, state) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.formNo);
            ps.setString(2, record.name);
            ps.setString(3, record.fname);
            ps.setString(4, record.dob);
            ps.setString(5, record.gender);
            ps.setString(6, record.email);
            ps.setString(7, record.marital);
            ps.setString(8, record.address);
            ps.setString(9, record.city);
            ps.setString(10, record.pinCode);
            ps.setString(11, record.state);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save signup", e);
        }
    }

    public void saveSignupTwo(SignupTwoRecord record) {
        String sql = "INSERT OR REPLACE INTO signup_two(form_no, religion, category, income, education, occupation, pan, aadhaar, senior_citizen, existing_account) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.formNo);
            ps.setString(2, record.religion);
            ps.setString(3, record.category);
            ps.setString(4, record.income);
            ps.setString(5, record.education);
            ps.setString(6, record.occupation);
            ps.setString(7, record.pan);
            ps.setString(8, record.aadhaar);
            ps.setString(9, record.seniorCitizen);
            ps.setString(10, record.existingAccount);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save signup page 2", e);
        }
    }

    public AccountRecord createAccount(String formNo, String accountType, String services, String cardNo, String pin) {
        String sql = "INSERT INTO account(form_no, account_type, services, card_no, pin) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, formNo);
            ps.setString(2, accountType);
            ps.setString(3, services);
            ps.setString(4, cardNo);
            ps.setString(5, pin);
            ps.executeUpdate();
            return new AccountRecord(formNo, accountType, services, cardNo, pin);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create account", e);
        }
    }

    public boolean validateLogin(String cardNo, String pin) {
        String sql = "SELECT 1 FROM account WHERE card_no = ? AND pin = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cardNo);
            ps.setString(2, pin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void addTransaction(String pin, Date date, String type, String amount) {
        int numericAmount;
        try {
            numericAmount = Integer.parseInt(amount.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Amount must be a number", ex);
        }

        String sql = "INSERT INTO transactions(pin, date_ms, type, amount) VALUES (?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            ps.setLong(2, date.getTime());
            ps.setString(3, type);
            ps.setInt(4, numericAmount);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to add transaction", e);
        }
    }

    public int getBalance(String pin) {
        String sql = "SELECT COALESCE(SUM(CASE WHEN LOWER(type) = 'deposit' THEN amount ELSE -amount END), 0) AS balance " +
                "FROM transactions WHERE pin = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("balance") : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    public List<TransactionRecord> getTransactions(String pin) {
        String sql = "SELECT date_ms, type, amount FROM transactions WHERE pin = ? ORDER BY date_ms";
        List<TransactionRecord> result = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = new Date(rs.getLong("date_ms"));
                    String type = rs.getString("type");
                    String amount = String.valueOf(rs.getInt("amount"));
                    result.add(new TransactionRecord(pin, d, type, amount));
                }
            }
        } catch (SQLException e) {
            // fall through and return what we have
        }
        return Collections.unmodifiableList(result);
    }

    public String getCardByPin(String pin) {
        String sql = "SELECT card_no FROM account WHERE pin = ? LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("card_no");
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public boolean updatePin(String oldPin, String newPin) {
        String updateAccount = "UPDATE account SET pin = ? WHERE pin = ?";
        String updateTx = "UPDATE transactions SET pin = ? WHERE pin = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psAcc = conn.prepareStatement(updateAccount);
                 PreparedStatement psTx = conn.prepareStatement(updateTx)) {
                psAcc.setString(1, newPin);
                psAcc.setString(2, oldPin);
                int updated = psAcc.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }

                psTx.setString(1, newPin);
                psTx.setString(2, oldPin);
                psTx.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean accountExists(String cardNo, String pin) {
        return validateLogin(cardNo, pin);
    }

    public void deposit(String pin, int amount) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO transactions(pin, date_ms, type, amount) VALUES (?, ?, 'Deposit', ?)")) {
            ps.setString(1, pin);
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, amount);
            ps.executeUpdate();
        }
    }

    public void withdraw(String pin, int amount) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO transactions(pin, date_ms, type, amount) VALUES (?, ?, 'Withdrawl', ?)")) {
            ps.setString(1, pin);
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, amount);
            ps.executeUpdate();
        }
    }

    public String getMiniStatement(String pin) throws SQLException {
        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT date_ms, type, amount FROM transactions WHERE pin = ? ORDER BY date_ms DESC LIMIT 10")) {
            ps.setString(1, pin);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{\"date\":\"").append(new Date(rs.getLong("date_ms"))).append("\",");
                    json.append("\"type\":\"").append(rs.getString("type")).append("\",");
                    json.append("\"amount\":").append(rs.getInt("amount")).append("}");
                }
            }
        }
        json.append("]");
        return json.toString();
    }

    public record SignupRecord(String formNo, String name, String fname, String dob, String gender,
                               String email, String marital, String address, String city, String pinCode, String state) {}

    public record SignupTwoRecord(String formNo, String religion, String category, String income, String education,
                                  String occupation, String pan, String aadhaar, String seniorCitizen, String existingAccount) {}

    public record AccountRecord(String formNo, String accountType, String services, String cardNo, String pin) {}

    public record TransactionRecord(String pin, Date date, String type, String amount) {}
}
