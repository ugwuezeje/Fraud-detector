package com.turing.fraud_detection.service;


import com.turing.fraud_detection.dto.PredictionRequest;
import com.turing.fraud_detection.dto.PredictionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final String modelVersion = "heuristic-v1.0";

    public String getModelVersion() {
        return modelVersion;
    }

    public PredictionResponse predict(PredictionRequest request) {
        Map<String, Object> features = request.getFeatures();
        double score = 0.0;

        if (features == null || features.isEmpty()) {
            logger.warn("Empty features in predict request, returning default non-fraud response");
            return new PredictionResponse(false, 0.0, modelVersion, "no-features");
        }

        double totalWeight = 0.0;

        Object amountObj = features.get("amount");
        if (amountObj != null) {
            Double amount = parseNumber(amountObj);
            if (amount != null) {
                double normalized = Math.min(1.0, Math.log10(1 + Math.abs(amount)) / 6.0);
                score += 0.6 * normalized;
                totalWeight += 0.6;
            }
        }

        Object countryObj = features.get("country");
        if (countryObj != null) {
            String country = countryObj.toString().toUpperCase();
            if ("NG".equals(country) || "PK".equals(country) || "UA".equals(country)) {
                score += 0.25;
                totalWeight += 0.25;
            } else {
                totalWeight += 0.0;
            }
        }

        Object failedLoginObj = features.get("failed_logins");
        if (failedLoginObj != null) {
            Double failed = parseNumber(failedLoginObj);
            if (failed != null && failed > 0) {
                double normalized = Math.min(1.0, failed / 10.0);
                score += 0.1 * normalized;
                totalWeight += 0.1;
            }
        }

        double baseline = 0.02;
        if (totalWeight > 0) {
            score = Math.min(1.0, (score / Math.max(1e-6, totalWeight)) + baseline);
        } else {
            score = baseline;
        }

        boolean isFraud = score >= 0.5;
        String explanation = String.format("heuristic(score=%.4f,threshold=0.5)", score);

        logger.debug("Predicted score: {}, isFraud: {}", score, isFraud);

        return new PredictionResponse(isFraud, round(score, 4), modelVersion, explanation);
    }

    private Double parseNumber(Object o) {
        try {
            if (o instanceof Number) {
                return ((Number) o).doubleValue();
            } else {
                return Double.parseDouble(o.toString());
            }
        } catch (Exception e) {
            logger.debug("Failed to parse number from object: {}", o);
            return null;
        }
    }

    private double round(double v, int places) {
        double scale = Math.pow(10, places);
        return Math.round(v * scale) / scale;
    }
}