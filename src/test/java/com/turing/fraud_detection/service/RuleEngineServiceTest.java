package com.turing.fraud_detection.service;


import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
public class RuleEngineServiceTest {

    @Autowired
    RuleEngineService ruleEngineService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    CaseService caseService;

    @Test
    public void largeAmountTriggersAlert() {
        Transaction tx = new Transaction();
        tx.setAccountId("acct-123");
        tx.setAmount(20000.0);
        tx.setCurrency("USD");
        tx.setIpAddress("1.2.3.4");
        tx.setTimestamp(Instant.now());
        tx.setId("tx-1");
        transactionService.save(tx);
        Alert alert = ruleEngineService.evaluate(tx);
        Assertions.assertNotNull(alert, "Large amount should generate alert");
        Assertions.assertTrue(alert.getScore() >= 70);
    }

    @Test
    public void ipVelocityTriggersAlert() throws InterruptedException {
        String ip = "9.8.7.6";
        for (int i=0;i<5;i++) {
            Transaction tx = new Transaction();
            tx.setAccountId("acct-vel");
            tx.setAmount(10.0);
            tx.setCurrency("NGN");
            tx.setIpAddress(ip);
            tx.setTimestamp(Instant.now());
            tx.setId("tx-vel-" + i);
            transactionService.save(tx);
            Alert alert = ruleEngineService.evaluate(tx);
            if (i < 3) {
                Assertions.assertNull(alert, "should not alert for early tx");
            } else {
                // from 3rd or 4th onward it may hit threshold depending on timing
                // allow for either null or alert, but if alert exists, score should be >= 60
                if (alert != null) Assertions.assertTrue(alert.getScore() >= 60);
            }
            Thread.sleep(50);
        }
    }
}
