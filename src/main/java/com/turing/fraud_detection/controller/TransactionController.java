package com.turing.fraud_detection.controller;


import com.turing.fraud_detection.model.Alert;
import com.turing.fraud_detection.model.Transaction;
import com.turing.fraud_detection.service.CaseService;
import com.turing.fraud_detection.service.RuleEngineService;
import com.turing.fraud_detection.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final RuleEngineService ruleEngineService;
    private final CaseService caseService;

    public TransactionController(TransactionService transactionService,
                                 RuleEngineService ruleEngineService,
                                 CaseService caseService) {
        this.transactionService = transactionService;
        this.ruleEngineService = ruleEngineService;
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<?> ingest(@RequestBody Transaction tx) {

        if (tx == null || tx.getAmount() == null || tx.getAccountId() == null) {
            return ResponseEntity.badRequest().body("invalid transaction payload");
        }
        tx.setId(UUID.randomUUID().toString());
        transactionService.save(tx);
        Alert alert = ruleEngineService.evaluate(tx);
        if (alert != null && alert.getScore() > 0) {
            caseService.createCaseForAlert(alert);
        }
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        Transaction t = transactionService.findById(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> listAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }
}
