package dev.tanmay.contactmanagementsystem.model.enums;

public enum MessagePriority {

    CRITICAL(80, 100, "Requires immediate attention", true,  true),
    HIGH    (60,  79, "Important, respond within 24h",  true,  false),
    MEDIUM  (35,  59, "Normal priority",                 false, false),
    LOW     ( 0,  34, "Low urgency, respond when free",  false, false);

    // ─── Fields ─────────────────────────────────────────────────────
    private final int     minScore;         // inclusive lower bound
    private final int     maxScore;         // inclusive upper bound
    private final String  description;      // human-readable label
    private final boolean sendNotification; // trigger NotificationService?
    private final boolean sendPushAlert;    // also fire push (FCM / phone)?

    // ─── Constructor ─────────────────────────────────────────────────
    MessagePriority(int minScore, int maxScore, String description,
                    boolean sendNotification, boolean sendPushAlert) {
        this.minScore         = minScore;
        this.maxScore         = maxScore;
        this.description      = description;
        this.sendNotification = sendNotification;
        this.sendPushAlert    = sendPushAlert;
    }

    // ─── Factory method ──────────────────────────────────────────────
    /**
     * Converts a raw integer score (0-100) to the matching priority label.
     *
     * DESIGN PATTERN: Factory method on enum — keeps scoring thresholds
     * in one place. PriorityEstimatorService calls this after computing
     * the weighted sum; it never hard-codes numbers itself.
     */
    public static MessagePriority fromScore(int score) {
        for (MessagePriority p : values()) {
            if (score >= p.minScore && score <= p.maxScore) {
                return p;
            }
        }
        return LOW; // safe fallback — never null
    }

    // ─── Getters ─────────────────────────────────────────────────────
    public int     getMinScore()         { return minScore; }
    public int     getMaxScore()         { return maxScore; }
    public String  getDescription()      { return description; }
    public boolean isSendNotification()  { return sendNotification; }
    public boolean isSendPushAlert()     { return sendPushAlert; }
}
