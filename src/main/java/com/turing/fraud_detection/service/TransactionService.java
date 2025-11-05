package com.turing.fraud_detection.service;

import com.turing.fraud_detection.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionService {

    private final Map<String, Transaction> store = new ConcurrentHashMap<>();

    public Transaction save(Transaction t) {
        store.put(t.getId(), t);
        return t;
    }

    public Transaction findById(String id) {
        return store.get(id);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Transaction> findByAccountId(String accountId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : store.values()) {
            if (accountId.equals(t.getAccountId())) out.add(t);
        }
        return out;
    }
}
