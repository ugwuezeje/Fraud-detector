package com.turing.fraud_detection.service;


import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
public class CaseServiceTest {

    @Autowired
    CaseService caseService;

    @Autowired
    TransactionService transactionService;

    @Test
    public void createAndCloseCase() {
        Transaction tx = new Transaction();
        tx.setId("c-tx-1");
        tx.setAccountId("acct-case");
        tx.setAmount(50000.0);
        tx.setTimestamp(Instant.now());
        transactionService.save(tx);
        Alert a = new Alert("a-1", tx.getId(), 90, "test");
        CaseService.Case c = caseService.createCaseForAlert(a);
        Assertions.assertNotNull(c);
        Assertions.assertEquals("OPEN", c.getStatus());
        caseService.closeCase(c.getId());
        Assertions.assertEquals("CLOSED", caseService.findById(c.getId()).getStatus());
    }
}
