package com.turing.fraud_detection.dto;



import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class PredictionRequest {

    @NotNull(message = "features must be provided (even empty map)")
    private Map<String, Object> features;

    public PredictionRequest() {
    }

    public PredictionRequest(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}