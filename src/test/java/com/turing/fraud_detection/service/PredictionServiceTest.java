package com.turing.fraud_detection.service;

import com.turing.fraud_detection.dto.PredictionRequest;
import com.turing.fraud_detection.dto.PredictionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PredictionServiceTest {

    private PredictionService service;

    @BeforeEach
    void setUp() {
        service = new PredictionService();
    }

    @Test
    void predict_withNullFeatures_returnsNonNullResponseAndNonFraud() {
        PredictionRequest req = new PredictionRequest();
        req.setFeatures(null);

        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.isFraud()).isFalse();
        assertThat(resp.getModelVersion()).isNotNull();
        assertThat(resp.getExplanation()).isNotNull();
    }

    @Test
    void predict_withEmptyFeatures_returnsBaselineScore() {
        PredictionRequest req = new PredictionRequest(new HashMap<>());

        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.isFraud()).isFalse();
    }

    @Test
    void predict_withNonNumericAmount_doesNotThrowAndProducesValidScore() {
        Map<String, Object> features = new HashMap<>();
        features.put("amount", "not-a-number");
        features.put("country", "US");
        PredictionRequest req = new PredictionRequest(features);

        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.getExplanation()).isNotNull();
    }

    @Test
    void predict_withLargeAmountHighRiskCountryHighFailedLogins_resultsInFraudLikely() {
        Map<String, Object> features = new HashMap<>();
        features.put("amount", 1_000_000);
        features.put("country", "NG");
        features.put("failed_logins", 50);

        PredictionRequest req = new PredictionRequest(features);
        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.isFraud()).isTrue();
    }

    @Test
    void predict_withZeroAmountAndSafeCountry_resultsInLowScoreAndNotFraud() {
        Map<String, Object> features = new HashMap<>();
        features.put("amount", 0);
        features.put("country", "US");
        features.put("failed_logins", 0);

        PredictionRequest req = new PredictionRequest(features);
        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.isFraud()).isFalse();
    }

    @Test
    void predict_withPartialData_stillProducesValidScore() {
        Map<String, Object> features = new HashMap<>();
        features.put("amount", 120.5);
        PredictionRequest req = new PredictionRequest(features);

        PredictionResponse resp = service.predict(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getScore()).isBetween(0.0, 1.0);
        assertThat(resp.getExplanation()).isNotNull().contains("heuristic");
    }
}