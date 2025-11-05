package com.turing.fraud_detection.service.rules;

import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;

public interface Rule {
    Alert evaluate(Transaction tx);
    String getName();
}
