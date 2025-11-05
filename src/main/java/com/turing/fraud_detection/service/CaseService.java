package com.turing.fraud_detection.service;

import com.turing.fraud_detection.model.Alert;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaseService {

    public static class Case {
        private final String id;
        private final String alertId;
        private final String txId;
        private final Instant createdAt;
        private String status;
        private final List<String> notes = new ArrayList<>();

        public Case(String id, String alertId, String txId) {
            this.id = id;
            this.alertId = alertId;
            this.txId = txId;
            this.createdAt = Instant.now();
            this.status = "OPEN";
        }

        public String getId() { return id; }
        public String getAlertId() { return alertId; }
        public String getTxId() { return txId; }
        public Instant getCreatedAt() { return createdAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<String> getNotes() { return notes; }
    }

    private final Map<String, Case> store = new ConcurrentHashMap<>();

    public Case createCaseForAlert(Alert alert) {
        String id = UUID.randomUUID().toString();
        Case c = new Case(id, alert.getId(), alert.getTransactionId());
        store.put(id, c);
        return c;
    }

    public Case findById(String id) {
        return store.get(id);
    }

    public List<Case> findAll() {
        return new ArrayList<>(store.values());
    }

    public void addNote(String caseId, String note) {
        Case c = store.get(caseId);
        if (c != null) c.getNotes().add(note);
    }

    public void closeCase(String caseId) {
        Case c = store.get(caseId);
        if (c != null) c.setStatus("CLOSED");
    }
}
