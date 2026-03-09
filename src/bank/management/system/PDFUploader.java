package bank.management.system;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * PDF Uploader - Uploads PDF to temporary hosting service
 * Returns public URL for WhatsApp sharing
 */
public class PDFUploader {
    
    /**
     * Upload PDF to file.io (temporary file hosting - 14 days)
     * @param pdfPath Local path to PDF file
     * @return Public URL or null if failed
     */
    public static String uploadToFileIO(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                System.err.println("PDF file not found: " + pdfPath);
                return null;
            }
            
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String LINE_FEED = "\r\n";
            
            URL url = new URL("https://file.io");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
            
            // Add file part
            String fileName = pdfFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: application/pdf").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            
            // Write file content
            Files.copy(pdfFile.toPath(), outputStream);
            outputStream.flush();
            
            writer.append(LINE_FEED);
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            
            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse JSON response to get link
                String jsonResponse = response.toString();
                if (jsonResponse.contains("\"link\"")) {
                    int linkStart = jsonResponse.indexOf("\"link\":\"") + 8;
                    int linkEnd = jsonResponse.indexOf("\"", linkStart);
                    String fileUrl = jsonResponse.substring(linkStart, linkEnd);
                    return fileUrl.replace("\\/", "/");
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error uploading PDF: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Upload PDF to tmpfiles.org (temporary file hosting)
     * @param pdfPath Local path to PDF file
     * @return Public URL or null if failed
     */
    public static String uploadToTmpFiles(String pdfPath) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                System.err.println("PDF file not found: " + pdfPath);
                return null;
            }
            
            String boundary = "----Boundary" + System.currentTimeMillis();
            String LINE_FEED = "\r\n";
            
            URL url = new URL("https://tmpfiles.org/api/v1/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            OutputStream outputStream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
            
            // Add file part
            String fileName = pdfFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: application/pdf").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            
            Files.copy(pdfFile.toPath(), outputStream);
            outputStream.flush();
            
            writer.append(LINE_FEED);
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            
            // Get response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // Parse JSON response
                String jsonResponse = response.toString();
                if (jsonResponse.contains("\"url\"")) {
                    int urlStart = jsonResponse.indexOf("\"url\":\"") + 7;
                    int urlEnd = jsonResponse.indexOf("\"", urlStart);
                    String fileUrl = jsonResponse.substring(urlStart, urlEnd);
                    // Convert to direct download URL
                    fileUrl = fileUrl.replace("tmpfiles.org/", "tmpfiles.org/dl/");
                    return fileUrl;
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error uploading PDF: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Upload PDF with retry mechanism - tries multiple services
     * @param pdfPath Local path to PDF file
     * @return Public URL or null if all attempts failed
     */
    public static String uploadPDF(String pdfPath) {
        System.out.println("\nUploading PDF to temporary hosting...");
        
        // Try tmpfiles.org first (usually more reliable for repeated downloads)
        System.out.print("Trying tmpfiles.org... ");
        String url = uploadToTmpFiles(pdfPath);
        if (url != null) {
            System.out.println("✓ Success!");
            return url;
        }
        System.out.println("✗ Failed");
        
        // Try file.io as backup
        System.out.print("Trying file.io... ");
        url = uploadToFileIO(pdfPath);
        if (url != null) {
            System.out.println("✓ Success!");
            return url;
        }
        System.out.println("✗ Failed");
        
        System.err.println("\nAll upload attempts failed.");
        System.err.println("Please upload PDF manually to Google Drive or Dropbox.");
        return null;
    }
}
