package com.turing.fraud_detection.service;


import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;
import com.turing.fraud_detection.service.rules.IpVelocityRule;
import com.turing.fraud_detection.service.rules.LargeAmountRule;
import com.turing.fraud_detection.service.rules.Rule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class RuleEngineService {

    private final List<Rule> rules = new ArrayList<>();

    @Value("${fraud.threshold.score:70}")
    private int thresholdScore;

    @PostConstruct
    public void init() {
        rules.add(new LargeAmountRule(10000.00));
        rules.add(new IpVelocityRule(3, 10)); // more than 3 tx in 10s
    }

    public Alert evaluate(Transaction tx) {
        Alert highest = null;
        for (Rule r : rules) {
            try {
                Alert a = r.evaluate(tx);
                if (a != null) {
                    if (highest == null || a.getScore() > highest.getScore()) {
                        highest = a;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (highest != null && highest.getScore() >= thresholdScore) {
            return highest;
        }
        return null;
    }

    public List<String> listRuleNames() {
        List<String> out = new ArrayList<>();
        for (Rule r : rules) out.add(r.getName());
        return out;
    }
}
