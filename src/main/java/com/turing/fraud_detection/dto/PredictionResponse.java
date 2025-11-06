package com.turing.fraud_detection.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionResponse {

    private boolean isFraud;
    private double score;
    private String modelVersion;
    private String explanation;

    public PredictionResponse() {
    }

    public PredictionResponse(boolean isFraud, double score, String modelVersion, String explanation) {
        this.isFraud = isFraud;
        this.score = score;
        this.modelVersion = modelVersion;
        this.explanation = explanation;
    }


    @JsonProperty("isFraud")
    public boolean isFraud() {
        return isFraud;
    }

    public void setFraud(boolean fraud) {
        isFraud = fraud;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}