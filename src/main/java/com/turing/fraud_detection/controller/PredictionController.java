package com.turing.fraud_detection.controller;


import com.turing.fraud_detection.dto.PredictionRequest;
import com.turing.fraud_detection.dto.PredictionResponse;
import com.turing.fraud_detection.service.PredictionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api")
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("modelVersion", predictionService.getModelVersion());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(@Valid @RequestBody PredictionRequest request) {
        logger.info("Received predict request with {} features", request.getFeatures() != null ? request.getFeatures().size() : 0);
        PredictionResponse response = predictionService.predict(request);
        return ResponseEntity.ok(response);
    }
}