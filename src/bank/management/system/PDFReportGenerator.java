package bank.management.system;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFReportGenerator {
    private static final String DB_URL = "jdbc:sqlite:db/bank.db";
    
    // Color scheme
    private static final BaseColor PRIMARY_COLOR = new BaseColor(41, 128, 185);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(52, 73, 94);
    private static final BaseColor ACCENT_COLOR = new BaseColor(231, 76, 60);
    private static final BaseColor SUCCESS_COLOR = new BaseColor(39, 174, 96);
    private static final BaseColor LIGHT_GRAY = new BaseColor(236, 240, 241);
    
    // Fonts
    private Font titleFont;
    private Font sectionFont;
    private Font headerFont;
    private Font normalFont;
    private Font boldFont;
    private Font smallFont;
    
    public PDFReportGenerator() {
        initializeFonts();
    }
    
    private void initializeFonts() {
        titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, PRIMARY_COLOR);
        sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, SECONDARY_COLOR);
        headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
    }
    
    /**
     * Generate a comprehensive PDF report of the database
     * @param outputPath The path where the PDF will be saved
     * @return true if PDF was generated successfully
     */
    public boolean generateReport(String outputPath) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            
            // Add header with logo/title
            addReportHeader(document);
            
            // Add document metadata
            addMetadata(document);
            
            // Add table of contents
            addTableOfContents(document);
            
            // Create connection
            Connection conn = DriverManager.getConnection(DB_URL);
            
            // Section 1: Executive Summary
            addExecutiveSummary(document, conn);
            
            // Section 2: Registered Users
            addRegisteredUsersSection(document, conn);
            
            // Section 3: Additional User Details
            addUserDetailsSection(document, conn);
            
            // Section 4: Active Accounts
            addActiveAccountsSection(document, conn);
            
            // Section 5: Transaction History
            addTransactionHistorySection(document, conn);
            
            // Section 6: Account Balances
            addBalancesSection(document, conn);
            
            // Section 7: Statistics & Analytics
            addStatisticsSection(document, conn);
            
            // Close connection and document
            conn.close();
            document.close();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void addReportHeader(Document document) throws DocumentException {
        // Create header table
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);
        
        // Title cell with background
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(PRIMARY_COLOR);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(20);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Paragraph title = new Paragraph("BANK MANAGEMENT SYSTEM", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, BaseColor.WHITE));
        title.setAlignment(Element.ALIGN_CENTER);
        titleCell.addElement(title);
        
        Paragraph subtitle = new Paragraph("Comprehensive Database Report", 
            FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.WHITE));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(5);
        titleCell.addElement(subtitle);
        
        headerTable.addCell(titleCell);
        document.add(headerTable);
        
        // Add generation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' hh:mm:ss a");
        Paragraph date = new Paragraph("Generated: " + dateFormat.format(new Date()), smallFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Add separator line
        addSeparatorLine(document, PRIMARY_COLOR);
    }
    
    private void addMetadata(Document document) {
        document.addTitle("Bank Management System - Database Report");
        document.addSubject("Comprehensive Database Analysis");
        document.addKeywords("Bank, Management, Report, Database");
        document.addAuthor("Bank Management System");
        document.addCreator("Bank Management System v1.0");
    }
    
    private void addTableOfContents(Document document) throws DocumentException {
        Paragraph tocTitle = new Paragraph("TABLE OF CONTENTS", sectionFont);
        tocTitle.setSpacingBefore(10);
        tocTitle.setSpacingAfter(15);
        document.add(tocTitle);
        
        String[] sections = {
            "1. Executive Summary",
            "2. Registered Users",
            "3. Additional User Details",
            "4. Active Accounts",
            "5. Transaction History",
            "6. Account Balances",
            "7. Statistics & Analytics"
        };
        
        for (String section : sections) {
            Paragraph item = new Paragraph("   " + section, normalFont);
            item.setSpacingBefore(3);
            document.add(item);
        }
        
        document.add(Chunk.NEWLINE);
        addSeparatorLine(document, PRIMARY_COLOR);
        document.add(Chunk.NEWLINE);
    }
    
    private void addExecutiveSummary(Document document, Connection conn) throws DocumentException, SQLException {
        addSectionHeader(document, "1. EXECUTIVE SUMMARY");
        
        // Get summary statistics
        int totalUsers = getCount(conn, "SELECT COUNT(*) FROM signup");
        int totalAccounts = getCount(conn, "SELECT COUNT(*) FROM account");
        int totalTransactions = getCount(conn, "SELECT COUNT(*) FROM transactions");
        long totalBalance = getTotalBalance(conn);
        
        // Create summary table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        table.setWidths(new float[]{3, 2});
        
        addSummaryRow(table, "Total Registered Users", String.valueOf(totalUsers), SUCCESS_COLOR);
        addSummaryRow(table, "Total Active Accounts", String.valueOf(totalAccounts), PRIMARY_COLOR);
        addSummaryRow(table, "Total Transactions", String.valueOf(totalTransactions), ACCENT_COLOR);
        addSummaryRow(table, "Total System Balance", "₹" + String.format("%,d", totalBalance), SUCCESS_COLOR);
        
        document.add(table);
        document.add(Chunk.NEWLINE);
    }
    
    private void addRegisteredUsersSection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "2. REGISTERED USERS");
        
        String query = "SELECT * FROM signup";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        int count = 0;
        while (rs.next()) {
            count++;
            
            // Create user card
            PdfPTable userTable = new PdfPTable(2);
            userTable.setWidthPercentage(100);
            userTable.setSpacingAfter(15);
            userTable.setWidths(new float[]{1, 2});
            
            // User header
            PdfPCell headerCell = new PdfPCell(new Phrase("USER #" + count, headerFont));
            headerCell.setBackgroundColor(SECONDARY_COLOR);
            headerCell.setColspan(2);
            headerCell.setPadding(8);
            userTable.addCell(headerCell);
            
            // User details
            addDetailRow(userTable, "Form No:", rs.getString("form_no"));
            addDetailRow(userTable, "Name:", rs.getString("name"));
            addDetailRow(userTable, "Father's Name:", rs.getString("fname"));
            addDetailRow(userTable, "Date of Birth:", rs.getString("dob"));
            addDetailRow(userTable, "Gender:", rs.getString("gender"));
            addDetailRow(userTable, "Email:", rs.getString("email"));
            addDetailRow(userTable, "Marital Status:", rs.getString("marital"));
            addDetailRow(userTable, "Address:", rs.getString("address"));
            addDetailRow(userTable, "City:", rs.getString("city"));
            addDetailRow(userTable, "Pin Code:", rs.getString("pin_code"));
            addDetailRow(userTable, "State:", rs.getString("state"));
            
            document.add(userTable);
        }
        
        if (count == 0) {
            document.add(new Paragraph("No registered users found.", normalFont));
        }
        
        rs.close();
        stmt.close();
        document.add(Chunk.NEWLINE);
    }
    
    private void addUserDetailsSection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "3. ADDITIONAL USER DETAILS");
        
        String query = "SELECT * FROM signup_two";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        int count = 0;
        while (rs.next()) {
            count++;
            
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingAfter(15);
            detailsTable.setWidths(new float[]{1, 2});
            
            PdfPCell headerCell = new PdfPCell(new Phrase("DETAILS #" + count, headerFont));
            headerCell.setBackgroundColor(SECONDARY_COLOR);
            headerCell.setColspan(2);
            headerCell.setPadding(8);
            detailsTable.addCell(headerCell);
            
            addDetailRow(detailsTable, "Form No:", rs.getString("form_no"));
            addDetailRow(detailsTable, "Religion:", rs.getString("religion"));
            addDetailRow(detailsTable, "Category:", rs.getString("category"));
            addDetailRow(detailsTable, "Income:", rs.getString("income"));
            addDetailRow(detailsTable, "Education:", rs.getString("education"));
            addDetailRow(detailsTable, "Occupation:", rs.getString("occupation"));
            addDetailRow(detailsTable, "PAN:", rs.getString("pan"));
            addDetailRow(detailsTable, "Aadhaar:", rs.getString("aadhaar"));
            addDetailRow(detailsTable, "Senior Citizen:", rs.getString("senior_citizen"));
            addDetailRow(detailsTable, "Existing Account:", rs.getString("existing_account"));
            
            document.add(detailsTable);
        }
        
        if (count == 0) {
            document.add(new Paragraph("No additional details found.", normalFont));
        }
        
        rs.close();
        stmt.close();
        document.add(Chunk.NEWLINE);
    }
    
    private void addActiveAccountsSection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "4. ACTIVE ACCOUNTS");
        
        String query = "SELECT * FROM account";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        int count = 0;
        while (rs.next()) {
            count++;
            
            PdfPTable accountTable = new PdfPTable(2);
            accountTable.setWidthPercentage(100);
            accountTable.setSpacingAfter(15);
            accountTable.setWidths(new float[]{1, 2});
            
            PdfPCell headerCell = new PdfPCell(new Phrase("ACCOUNT #" + count, headerFont));
            headerCell.setBackgroundColor(PRIMARY_COLOR);
            headerCell.setColspan(2);
            headerCell.setPadding(8);
            accountTable.addCell(headerCell);
            
            addDetailRow(accountTable, "Form No:", rs.getString("form_no"));
            addDetailRow(accountTable, "Account Type:", rs.getString("account_type"));
            addDetailRow(accountTable, "Services:", rs.getString("services"));
            addDetailRow(accountTable, "Card Number:", rs.getString("card_no"));
            addDetailRow(accountTable, "PIN:", rs.getString("pin"));
            addDetailRow(accountTable, "Created At:", rs.getString("created_at"));
            
            document.add(accountTable);
        }
        
        if (count == 0) {
            document.add(new Paragraph("No active accounts found.", normalFont));
        } else {
            Paragraph total = new Paragraph("Total Accounts: " + count, boldFont);
            total.setSpacingBefore(10);
            document.add(total);
        }
        
        rs.close();
        stmt.close();
        document.add(Chunk.NEWLINE);
    }
    
    private void addTransactionHistorySection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "5. TRANSACTION HISTORY");
        
        String query = "SELECT * FROM transactions ORDER BY id DESC";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        // Create transaction table
        PdfPTable transTable = new PdfPTable(5);
        transTable.setWidthPercentage(100);
        transTable.setSpacingAfter(15);
        transTable.setWidths(new float[]{1, 2, 3, 2, 2});
        
        // Headers
        addTableHeader(transTable, "ID");
        addTableHeader(transTable, "PIN");
        addTableHeader(transTable, "Date & Time");
        addTableHeader(transTable, "Type");
        addTableHeader(transTable, "Amount");
        
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
            
            addTableCell(transTable, String.valueOf(id));
            addTableCell(transTable, pin);
            addTableCell(transTable, sdf.format(date));
            
            // Color code transaction type
            PdfPCell typeCell = new PdfPCell(new Phrase(type, normalFont));
            typeCell.setPadding(5);
            if (type.equalsIgnoreCase("Deposit")) {
                typeCell.setBackgroundColor(new BaseColor(212, 237, 218));
                totalDeposits += amount;
            } else {
                typeCell.setBackgroundColor(new BaseColor(248, 215, 218));
                totalWithdrawals += amount;
            }
            transTable.addCell(typeCell);
            
            addTableCell(transTable, "₹" + String.format("%,d", amount));
        }
        
        if (count == 0) {
            document.add(new Paragraph("No transactions found.", normalFont));
        } else {
            document.add(transTable);
            
            // Add summary
            Paragraph summary = new Paragraph();
            summary.add(new Chunk("Total Transactions: " + count + "\n", boldFont));
            summary.add(new Chunk("Total Deposits: ₹" + String.format("%,d", totalDeposits) + "\n", 
                FontFactory.getFont(FontFactory.HELVETICA, 10, SUCCESS_COLOR)));
            summary.add(new Chunk("Total Withdrawals: ₹" + String.format("%,d", totalWithdrawals), 
                FontFactory.getFont(FontFactory.HELVETICA, 10, ACCENT_COLOR)));
            summary.setSpacingBefore(10);
            document.add(summary);
        }
        
        rs.close();
        stmt.close();
        document.add(Chunk.NEWLINE);
    }
    
    private void addBalancesSection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "6. ACCOUNT BALANCES");
        
        String query = "SELECT a.card_no, a.pin, s.name, " +
                       "COALESCE(SUM(CASE WHEN t.type = 'Deposit' THEN t.amount ELSE -t.amount END), 0) as balance " +
                       "FROM account a " +
                       "JOIN signup s ON a.form_no = s.form_no " +
                       "LEFT JOIN transactions t ON a.pin = t.pin " +
                       "GROUP BY a.card_no, a.pin, s.name " +
                       "ORDER BY balance DESC";
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        // Create balance table
        PdfPTable balanceTable = new PdfPTable(4);
        balanceTable.setWidthPercentage(100);
        balanceTable.setSpacingAfter(15);
        balanceTable.setWidths(new float[]{3, 2, 4, 2});
        
        // Headers
        addTableHeader(balanceTable, "Card Number");
        addTableHeader(balanceTable, "PIN");
        addTableHeader(balanceTable, "Account Holder");
        addTableHeader(balanceTable, "Balance");
        
        int count = 0;
        long totalBalance = 0;
        
        while (rs.next()) {
            count++;
            String cardNo = rs.getString("card_no");
            String pin = rs.getString("pin");
            String name = rs.getString("name");
            long balance = rs.getLong("balance");
            
            totalBalance += balance;
            
            addTableCell(balanceTable, cardNo);
            addTableCell(balanceTable, pin);
            addTableCell(balanceTable, name);
            
            PdfPCell balanceCell = new PdfPCell(new Phrase("₹" + String.format("%,d", balance), boldFont));
            balanceCell.setPadding(5);
            balanceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if (balance > 0) {
                balanceCell.setBackgroundColor(new BaseColor(212, 237, 218));
            } else if (balance < 0) {
                balanceCell.setBackgroundColor(new BaseColor(248, 215, 218));
            }
            balanceTable.addCell(balanceCell);
        }
        
        if (count == 0) {
            document.add(new Paragraph("No accounts found.", normalFont));
        } else {
            document.add(balanceTable);
            
            // Add total
            Paragraph total = new Paragraph("Total System Balance: ₹" + String.format("%,d", totalBalance), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, SUCCESS_COLOR));
            total.setSpacingBefore(10);
            document.add(total);
        }
        
        rs.close();
        stmt.close();
        document.add(Chunk.NEWLINE);
    }
    
    private void addStatisticsSection(Document document, Connection conn) 
            throws DocumentException, SQLException {
        addSectionHeader(document, "7. STATISTICS & ANALYTICS");
        
        // Get various statistics
        int avgTransactionsPerAccount = getAverageTransactionsPerAccount(conn);
        long avgBalance = getAverageBalance(conn);
        String mostActiveAccount = getMostActiveAccount(conn);
        
        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingAfter(20);
        statsTable.setWidths(new float[]{3, 2});
        
        addSummaryRow(statsTable, "Average Transactions per Account", String.valueOf(avgTransactionsPerAccount), PRIMARY_COLOR);
        addSummaryRow(statsTable, "Average Account Balance", "₹" + String.format("%,d", avgBalance), SUCCESS_COLOR);
        addSummaryRow(statsTable, "Most Active Account (PIN)", mostActiveAccount, ACCENT_COLOR);
        
        document.add(statsTable);
        
        // Add report footer
        addReportFooter(document);
    }
    
    private void addReportFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        addSeparatorLine(document, PRIMARY_COLOR);
        
        Paragraph footer = new Paragraph();
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.add(new Chunk("End of Report\n", boldFont));
        footer.add(new Chunk("This report is confidential and intended for authorized personnel only.\n", smallFont));
        footer.add(new Chunk("© " + new SimpleDateFormat("yyyy").format(new Date()) + " Bank Management System. All rights reserved.", smallFont));
        
        document.add(footer);
    }
    
    // Helper methods
    private void addSectionHeader(Document document, String title) throws DocumentException {
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }
    
    private void addSeparatorLine(Document document, BaseColor color) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineColor(color);
        line.setLineWidth(2);
        document.add(line);
    }
    
    private void addSummaryRow(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setPadding(10);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color)));
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setBackgroundColor(LIGHT_GRAY);
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }
    
    private void addDetailRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(LIGHT_GRAY);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
    
    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(SECONDARY_COLOR);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, normalFont));
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private int getCount(Connection conn, String query) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }
    
    private long getTotalBalance(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END), 0) as total " +
                       "FROM transactions";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        long total = rs.getLong("total");
        rs.close();
        stmt.close();
        return total;
    }
    
    private int getAverageTransactionsPerAccount(Connection conn) throws SQLException {
        String query = "SELECT CAST(AVG(trans_count) AS INTEGER) as avg_trans FROM " +
                       "(SELECT COUNT(*) as trans_count FROM transactions GROUP BY pin)";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int avg = rs.getInt("avg_trans");
            rs.close();
            stmt.close();
            return avg;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    private long getAverageBalance(Connection conn) throws SQLException {
        String query = "SELECT CAST(AVG(balance) AS INTEGER) as avg_balance FROM " +
                       "(SELECT COALESCE(SUM(CASE WHEN type = 'Deposit' THEN amount ELSE -amount END), 0) as balance " +
                       "FROM transactions GROUP BY pin)";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            long avg = rs.getLong("avg_balance");
            rs.close();
            stmt.close();
            return avg;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    private String getMostActiveAccount(Connection conn) throws SQLException {
        String query = "SELECT pin, COUNT(*) as trans_count FROM transactions " +
                       "GROUP BY pin ORDER BY trans_count DESC LIMIT 1";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                String pin = rs.getString("pin");
                rs.close();
                stmt.close();
                return pin;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            // Ignore
        }
        return "N/A";
    }
}
