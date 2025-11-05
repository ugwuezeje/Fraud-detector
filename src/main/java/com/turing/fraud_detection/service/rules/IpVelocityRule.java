package com.turing.fraud_detection.service.rules;


import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class IpVelocityRule implements Rule {

    private final int maxTx;
    private final int windowSeconds;
    private final Map<String, List<Instant>> ipMap = new ConcurrentHashMap<>();

    public IpVelocityRule(int maxTx, int windowSeconds) {
        this.maxTx = maxTx;
        this.windowSeconds = windowSeconds;
    }

    @Override
    public Alert evaluate(Transaction tx) {
        if (tx.getIpAddress() == null) return null;
        List<Instant> list = ipMap.computeIfAbsent(tx.getIpAddress(), k -> Collections.synchronizedList(new ArrayList<>()));
        Instant now = tx.getTimestamp() == null ? Instant.now() : tx.getTimestamp();
        list.add(now);
        list.removeIf(t -> Duration.between(t, now).getSeconds() > windowSeconds);
        if (list.size() > maxTx) {
            return new Alert(UUID.randomUUID().toString(), tx.getId(), 60,
                    "velocity: " + list.size() + " tx in " + windowSeconds + "s");
        }
        return null;
    }

    @Override
    public String getName() {
        return "IpVelocityRule";
    }
}
