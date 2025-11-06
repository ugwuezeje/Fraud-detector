package com.turing.fraud_detection.controller;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.turing.fraud_detection.dto.PredictionResponse;
import com.turing.fraud_detection.service.PredictionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PredictionController.class)
class PredictionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PredictionService predictionService;

    @Test
    @DisplayName("GET /api/health returns status and modelVersion")
    void healthEndpoint_returnsStatusAndModelVersion() throws Exception {
        given(predictionService.getModelVersion()).willReturn("test-model-v1");

        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.modelVersion").value("test-model-v1"));
    }

    @Test
    @DisplayName("POST /api/predict with valid payload returns prediction JSON")
    void predictEndpoint_withValidPayload_returnsPrediction() throws Exception {
        PredictionResponse mockResp = new PredictionResponse(true, 0.87, "v1", "test-explanation");
        given(predictionService.predict(ArgumentMatchers.any())).willReturn(mockResp);

        String payload = objectMapper.writeValueAsString(Map.of("features", Map.of(
                "amount", 123.45,
                "country", "NG"
        )));

        mvc.perform(post("/api/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isFraud").value(true))
                .andExpect(jsonPath("$.score").value(0.87))
                .andExpect(jsonPath("$.modelVersion").value("v1"))
                .andExpect(jsonPath("$.explanation").value("test-explanation"));
    }

    @Test
    @DisplayName("POST /api/predict with empty-features returns 200 and valid JSON")
    void predictEndpoint_withEmptyFeatures_returnsOk() throws Exception {
        PredictionResponse mockResp = new PredictionResponse(false, 0.02, "v1", "empty-features");
        given(predictionService.predict(ArgumentMatchers.any())).willReturn(mockResp);

        String payload = objectMapper.writeValueAsString(Map.of("features", Map.of()));

        mvc.perform(post("/api/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFraud").value(false))
                .andExpect(jsonPath("$.score").isNumber());
    }

    @Test
    @DisplayName("POST /api/predict with missing 'features' property returns 400 (validation)")
    void predictEndpoint_missingFeaturesProperty_returnsBadRequest() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of());

        mvc.perform(post("/api/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/predict with malformed JSON returns 400")
    void predictEndpoint_malformedJson_returnsBadRequest() throws Exception {
        String payload = "{\"features\": {\"amount\": 123,,}}";

        mvc.perform(post("/api/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}