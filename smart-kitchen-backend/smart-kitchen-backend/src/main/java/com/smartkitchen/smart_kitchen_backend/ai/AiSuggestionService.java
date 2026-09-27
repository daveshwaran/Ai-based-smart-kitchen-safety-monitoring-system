package com.smartkitchen.smart_kitchen_backend.ai;

import com.smartkitchen.smart_kitchen_backend.entity.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiSuggestionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateSuggestion(SensorData data) {

        String prompt = buildPrompt(data);

        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = apiUrl + "?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            return extractText(response.getBody());

        } catch (Exception e) {
            return fallbackSuggestion(data);
        }
    }

    private String buildPrompt(SensorData data) {

        return """
                You are a smart kitchen safety assistant.
                Give a short safety suggestion in 2 to 3 lines.
                Do not give medical advice.
                Do not use long explanation.

                Sensor readings:
                Gas value: %d
                Flame value: %d
                Temperature: %.2f Celsius
                Current sensor value: %d
                Risk level: %s
                Alert message: %s

                Note:
                Flame value 1 means flame detected.
                Flame value 0 means no flame.
                """
                .formatted(
                        data.getGasValue(),
                        data.getFlameValue(),
                        data.getTemperature(),
                        data.getCurrentValue(),
                        data.getRiskLevel(),
                        data.getAlertMessage()
                );
    }

    private String extractText(Map responseBody) {

        if (responseBody == null) {
            return "AI suggestion unavailable.";
        }

        try {
            List candidates = (List) responseBody.get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");
            List parts = (List) content.get("parts");
            Map part = (Map) parts.get(0);

            return part.get("text").toString();

        } catch (Exception e) {
            return "AI suggestion unavailable.";
        }
    }

    private String fallbackSuggestion(SensorData data) {

        if ("CRITICAL".equals(data.getRiskLevel())) {
            return "Critical risk detected. Turn off the gas supply, switch off nearby appliances, open windows, and move away safely.";
        }

        if ("HIGH".equals(data.getRiskLevel())) {
            return "High risk detected. Check gas leakage or fire source immediately and avoid using electrical switches.";
        }

        if ("MEDIUM".equals(data.getRiskLevel())) {
            return "Warning condition detected. Monitor the kitchen carefully and check gas, flame, and temperature levels.";
        }

        return "Kitchen condition is safe. Continue monitoring sensor readings regularly.";
    }
}