package bank.management.system;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

/**
 * WhatsApp Service using Twilio API
 * Sends messages, PDFs, and notifications via WhatsApp
 */
public class WhatsAppService {
    private Properties config;
    private String accountSid;
    private String authToken;
    private String fromNumber;
    private String toNumber;
    private boolean enabled;
    
    private static final String TWILIO_API_URL = "https://api.twilio.com/2010-04-01";
    
    public WhatsAppService() {
        loadConfiguration();
    }
    
    /**
     * Load WhatsApp/Twilio configuration from whatsapp.properties
     */
    private void loadConfiguration() {
        config = new Properties();
        try (FileInputStream fis = new FileInputStream("whatsapp.properties")) {
            config.load(fis);
            
            accountSid = config.getProperty("twilio.account.sid");
            authToken = config.getProperty("twilio.auth.token");
            fromNumber = config.getProperty("twilio.whatsapp.from");
            toNumber = config.getProperty("whatsapp.recipient.number");
            enabled = Boolean.parseBoolean(config.getProperty("whatsapp.enabled", "true"));
            
        } catch (IOException e) {
            System.err.println("Error loading WhatsApp configuration: " + e.getMessage());
            System.err.println("Please ensure whatsapp.properties file exists in the project root.");
            enabled = false;
        }
    }
    
    /**
     * Send a text message via WhatsApp
     * @param message The message to send
     * @return true if successful
     */
    public boolean sendMessage(String message) {
        if (!enabled) {
            System.err.println("WhatsApp service is disabled.");
            return false;
        }
        
        if (!validateConfiguration()) {
            return false;
        }
        
        try {
            String url = TWILIO_API_URL + "/Accounts/" + accountSid + "/Messages.json";
            
            // Prepare request body
            String body = "From=" + URLEncoder.encode(fromNumber, "UTF-8") +
                         "&To=" + URLEncoder.encode(toNumber, "UTF-8") +
                         "&Body=" + URLEncoder.encode(message, "UTF-8");
            
            // Send request
            String response = sendTwilioRequest(url, body);
            
            if (response != null && response.contains("\"status\"")) {
                System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
                System.out.println("║              WHATSAPP MESSAGE SENT SUCCESSFULLY!                   ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════╝");
                System.out.println("\nMessage sent to: " + toNumber);
                return true;
            } else {
                System.err.println("Failed to send WhatsApp message.");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error sending WhatsApp message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send a PDF document via WhatsApp
     * @param message The message text
     * @param pdfPath The path to the PDF file
     * @param pdfUrl The public URL where the PDF is hosted (required by Twilio)
     * @return true if successful
     */
    public boolean sendPDF(String message, String pdfPath, String pdfUrl) {
        if (!enabled) {
            System.err.println("WhatsApp service is disabled.");
            return false;
        }
        
        if (!validateConfiguration()) {
            return false;
        }
        
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            System.err.println("PDF file not found: " + pdfPath);
            return false;
        }
        
        try {
            String url = TWILIO_API_URL + "/Accounts/" + accountSid + "/Messages.json";
            
            // Prepare request body with media
            String body = "From=" + URLEncoder.encode(fromNumber, "UTF-8") +
                         "&To=" + URLEncoder.encode(toNumber, "UTF-8") +
                         "&Body=" + URLEncoder.encode(message, "UTF-8") +
                         "&MediaUrl=" + URLEncoder.encode(pdfUrl, "UTF-8");
            
            // Send request
            String response = sendTwilioRequest(url, body);
            
            if (response != null && response.contains("\"status\"")) {
                // Check if there's an actual error (error_code is not null)
                if (response.contains("\"error_code\":") && 
                    !response.contains("\"error_code\": null") &&
                    !response.contains("\"error_code\":null")) {
                    System.err.println("\n⚠ Twilio API returned an error:");
                    System.err.println(response);
                    return false;
                }
                
                // Check accepted status
                if (response.contains("\"status\":\"queued\"") ||
                    response.contains("\"status\":\"accepted\"") ||
                    response.contains("\"status\":\"sending\"") ||
                    response.contains("\"status\":\"sent\"") ||
                    response.contains("\"status\": \"queued\"") ||
                    response.contains("\"status\": \"accepted\"") ||
                    response.contains("\"status\": \"sending\"") ||
                    response.contains("\"status\": \"sent\"")) {
                    
                    System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
                    System.out.println("║              WHATSAPP PDF SENT SUCCESSFULLY!                       ║");
                    System.out.println("╚════════════════════════════════════════════════════════════════════╝");
                    System.out.println("\nPDF sent to: " + toNumber);
                    System.out.println("File: " + pdfFile.getName());
                    System.out.println("\n⚠ Note: If PDF doesn't appear in WhatsApp, the media URL may be blocked.");
                    System.out.println("   Twilio sandbox has restrictions on some file hosting services.");
                    return true;
                } else {
                    System.err.println("Twilio returned unexpected status for media message.");
                    return false;
                }
            } else {
                System.err.println("Failed to send WhatsApp PDF.");
                System.err.println("Response: " + response);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error sending WhatsApp PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send PDF link as text message (more reliable than media attachment)
     * @param message The message body
     * @param pdfUrl The public URL of the PDF
     * @return true if successful
     */
    public boolean sendPDFLink(String message, String pdfUrl) {
        if (!enabled) {
            System.err.println("WhatsApp service is disabled.");
            return false;
        }
        
        if (!validateConfiguration()) {
            return false;
        }
        
        // Create message with clickable link
        String fullMessage = message + "\n\n📥 *Download PDF:*\n" + pdfUrl;
        
        return sendMessage(fullMessage);
    }
    
    /**
     * Send a formatted report message
     * @param reportData The report data to send
     * @return true if successful
     */
    public boolean sendFormattedReport(String reportData) {
        String header = config.getProperty("whatsapp.template.header", "🏦 Bank Management System");
        String footer = config.getProperty("whatsapp.template.footer", "_Automated System Message_");
        
        String formattedMessage = header + "\n\n" + reportData + "\n\n" + footer;
        
        return sendMessage(formattedMessage);
    }
    
    /**
     * Send transaction alert
     * @param pin Account PIN
     * @param type Transaction type (Deposit/Withdrawal)
     * @param amount Transaction amount
     * @param balance Current balance
     * @return true if successful
     */
    public boolean sendTransactionAlert(String pin, String type, int amount, int balance) {
        StringBuilder message = new StringBuilder();
        
        message.append("🏦 *TRANSACTION ALERT*\n\n");
        message.append("═══════════════════════\n");
        message.append("Account: ").append("****").append(pin.substring(Math.max(0, pin.length() - 4))).append("\n");
        message.append("Type: ").append(type.equalsIgnoreCase("Deposit") ? "💰 Deposit" : "💸 Withdrawal").append("\n");
        message.append("Amount: ₹").append(String.format("%,d", amount)).append("\n");
        message.append("Balance: ₹").append(String.format("%,d", balance)).append("\n");
        message.append("═══════════════════════\n");
        message.append("Time: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
        
        return sendMessage(message.toString());
    }
    
    /**
     * Send balance inquiry response
     * @param pin Account PIN
     * @param balance Current balance
     * @return true if successful
     */
    public boolean sendBalanceInquiry(String pin, int balance) {
        StringBuilder message = new StringBuilder();
        
        message.append("🏦 *BALANCE INQUIRY*\n\n");
        message.append("═══════════════════════\n");
        message.append("Account: ****").append(pin.substring(Math.max(0, pin.length() - 4))).append("\n");
        message.append("Available Balance: ₹").append(String.format("%,d", balance)).append("\n");
        message.append("═══════════════════════\n");
        message.append("Time: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
        
        return sendMessage(message.toString());
    }
    
    /**
     * Send PIN change confirmation
     * @param cardNumber Card number (masked)
     * @return true if successful
     */
    public boolean sendPINChangeConfirmation(String cardNumber) {
        StringBuilder message = new StringBuilder();
        
        message.append("🔐 *PIN CHANGE CONFIRMATION*\n\n");
        message.append("═══════════════════════\n");
        message.append("Your ATM PIN has been changed successfully.\n\n");
        message.append("Card: ****-****-****-").append(cardNumber.substring(Math.max(0, cardNumber.length() - 4))).append("\n");
        message.append("═══════════════════════\n");
        message.append("Time: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date())).append("\n\n");
        message.append("If you did not make this change, please contact us immediately.");
        
        return sendMessage(message.toString());
    }
    
    /**
     * Send a Twilio API request
     * @param url API endpoint URL
     * @param body Request body
     * @return Response string
     */
    private String sendTwilioRequest(String url, String body) throws IOException {
        HttpURLConnection connection = null;
        
        try {
            URL apiUrl = new URL(url);
            connection = (HttpURLConnection) apiUrl.openConnection();
            
            // Set up connection
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Add Basic Authentication
            String auth = accountSid + ":" + authToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            
            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            InputStream inputStream = responseCode < 400 ? connection.getInputStream() : connection.getErrorStream();
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            if (responseCode >= 400) {
                System.err.println("Twilio API Error (" + responseCode + "): " + response.toString());
                return null;
            }
            
            return response.toString();
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Validate configuration
     * @return true if configuration is valid
     */
    private boolean validateConfiguration() {
        if (accountSid == null || authToken == null || fromNumber == null || toNumber == null) {
            System.err.println("WhatsApp configuration is incomplete. Please check whatsapp.properties file.");
            return false;
        }
        
        if (accountSid.equals("YOUR_ACCOUNT_SID_HERE") || authToken.equals("YOUR_AUTH_TOKEN_HERE")) {
            System.err.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.err.println("║                  WHATSAPP NOT CONFIGURED                           ║");
            System.err.println("╚════════════════════════════════════════════════════════════════════╝");
            System.err.println("\nPlease configure your Twilio WhatsApp settings in whatsapp.properties:");
            System.err.println("1. Create a Twilio account at https://www.twilio.com/");
            System.err.println("2. Get your Account SID and Auth Token");
            System.err.println("3. Set up WhatsApp Sandbox or get approved for WhatsApp Business API");
            System.err.println("4. Update whatsapp.properties with your credentials\n");
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if WhatsApp service is enabled and configured
     * @return true if enabled and configured
     */
    public boolean isEnabled() {
        return enabled && validateConfiguration();
    }
}
