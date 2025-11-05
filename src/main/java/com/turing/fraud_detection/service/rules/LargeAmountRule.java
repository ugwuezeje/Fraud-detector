package com.turing.fraud_detection.service.rules;

import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;

import java.util.UUID;

public class LargeAmountRule implements Rule {

    private final double threshold;

    public LargeAmountRule(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public Alert evaluate(Transaction tx) {
        if (tx.getAmount() == null) return null;
        if (tx.getAmount() > threshold) {
            Alert a = new Alert(UUID.randomUUID().toString(), tx.getId(), 80,
                    "amount > " + threshold);
            return a;
        }
        return null;
    }

    @Override
    public String getName() {
        return "LargeAmountRule";
    }
}
