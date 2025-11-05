package com.turing.fraud_detection.model;

public class Alert {
    private String id;
    private String transactionId;
    private int score;
    private String reason;

    public Alert() {}

    public Alert(String id, String transactionId, int score, String reason) {
        this.id = id;
        this.transactionId = transactionId;
        this.score = score;
        this.reason = reason;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
