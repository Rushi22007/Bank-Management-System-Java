package bank.management.system;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Notification Scheduler
 * Handles scheduled and queued notifications for Email and WhatsApp
 */
public class NotificationScheduler {
    
    private final NotificationService notificationService;
    private final Queue<NotificationTask> notificationQueue;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executorService;
    private boolean isRunning;
    
    public NotificationScheduler() {
        this.notificationService = new NotificationService();
        this.notificationQueue = new ConcurrentLinkedQueue<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.executorService = Executors.newFixedThreadPool(5);
        this.isRunning = false;
    }
    
    /**
     * Start the notification scheduler
     */
    public void start() {
        if (!isRunning) {
            isRunning = true;
            System.out.println("✅ Notification Scheduler started");
            
            // Process queue every 5 seconds
            scheduler.scheduleAtFixedRate(this::processQueue, 0, 5, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Stop the notification scheduler
     */
    public void stop() {
        isRunning = false;
        scheduler.shutdown();
        executorService.shutdown();
        System.out.println("❌ Notification Scheduler stopped");
    }
    
    /**
     * Queue a notification for immediate processing
     */
    public void queueNotification(String recipient, String subject, String message) {
        NotificationTask task = new NotificationTask(
            recipient, subject, message, NotificationType.BOTH, LocalDateTime.now()
        );
        notificationQueue.add(task);
        System.out.println("📬 Notification queued for: " + recipient);
    }
    
    /**
     * Queue email notification
     */
    public void queueEmailNotification(String recipient, String subject, String message) {
        NotificationTask task = new NotificationTask(
            recipient, subject, message, NotificationType.EMAIL, LocalDateTime.now()
        );
        notificationQueue.add(task);
        System.out.println("📧 Email notification queued for: " + recipient);
    }
    
    /**
     * Queue WhatsApp notification
     */
    public void queueWhatsAppNotification(String recipient, String message) {
        NotificationTask task = new NotificationTask(
            recipient, "WhatsApp Message", message, NotificationType.WHATSAPP, LocalDateTime.now()
        );
        notificationQueue.add(task);
        System.out.println("💬 WhatsApp notification queued for: " + recipient);
    }
    
    /**
     * Schedule notification for specific time
     */
    public void scheduleNotification(String recipient, String subject, String message, 
                                     long delaySeconds) {
        scheduler.schedule(() -> {
            try {
                notificationService.sendNotification(recipient, subject, message);
                System.out.println("✅ Scheduled notification sent to: " + recipient);
            } catch (Exception e) {
                System.err.println("Failed to send scheduled notification: " + e.getMessage());
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }
    
    /**
     * Schedule recurring notification
     */
    public void scheduleRecurringNotification(String recipient, String subject, String message,
                                             long initialDelaySeconds, long intervalSeconds) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                notificationService.sendNotification(recipient, subject, message);
                System.out.println("✅ Recurring notification sent to: " + recipient);
            } catch (Exception e) {
                System.err.println("Failed to send recurring notification: " + e.getMessage());
            }
        }, initialDelaySeconds, intervalSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * Process notification queue
     */
    private void processQueue() {
        if (notificationQueue.isEmpty()) {
            return;
        }
        
        NotificationTask task = notificationQueue.poll();
        if (task != null) {
            executorService.submit(() -> {
                try {
                    processNotificationTask(task);
                } catch (Exception e) {
                    System.err.println("Error processing notification task: " + e.getMessage());
                    // Re-queue if failed (max 3 retries)
                    if (task.getRetries() < 3) {
                        task.incrementRetries();
                        notificationQueue.add(task);
                        System.out.println("🔄 Notification requeued (Attempt " + task.getRetries() + "/3)");
                    }
                }
            });
        }
    }
    
    /**
     * Process individual notification task
     */
    private void processNotificationTask(NotificationTask task) throws Exception {
        switch (task.getType()) {
            case EMAIL:
                notificationService.sendEmail(task.getRecipient(), task.getSubject(), task.getMessage());
                break;
            case WHATSAPP:
                notificationService.sendWhatsApp(task.getRecipient(), task.getMessage());
                break;
            case BOTH:
                notificationService.sendNotification(task.getRecipient(), task.getSubject(), task.getMessage());
                break;
        }
    }
    
    /**
     * Get queue size
     */
    public int getQueueSize() {
        return notificationQueue.size();
    }
    
    /**
     * Clear queue
     */
    public void clearQueue() {
        notificationQueue.clear();
        System.out.println("🗑️  Notification queue cleared");
    }
    
    /**
     * Check if scheduler is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Inner class for notification tasks
     */
    private static class NotificationTask {
        private final String recipient;
        private final String subject;
        private final String message;
        private final NotificationType type;
        private final LocalDateTime createdAt;
        private int retries;
        
        public NotificationTask(String recipient, String subject, String message,
                              NotificationType type, LocalDateTime createdAt) {
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.type = type;
            this.createdAt = createdAt;
            this.retries = 0;
        }
        
        public String getRecipient() { return recipient; }
        public String getSubject() { return subject; }
        public String getMessage() { return message; }
        public NotificationType getType() { return type; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getRetries() { return retries; }
        public void incrementRetries() { this.retries++; }
    }
    
    /**
     * Notification type enum
     */
    private enum NotificationType {
        EMAIL, WHATSAPP, BOTH
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        NotificationScheduler scheduler = new NotificationScheduler();
        scheduler.start();
        
        // Queue some notifications
        scheduler.queueEmailNotification(
            "customer@example.com",
            "Transaction Alert",
            "Your account has been accessed"
        );
        
        scheduler.queueWhatsAppNotification(
            "+919876543210",
            "💰 Deposit of ₹5000 has been successfully credited to your account"
        );
        
        // Schedule a notification for 10 seconds from now
        scheduler.scheduleNotification(
            "customer@example.com",
            "Delayed Notification",
            "This notification was scheduled",
            10
        );
        
        try {
            Thread.sleep(30000); // Keep scheduler running for 30 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        scheduler.stop();
    }
}
