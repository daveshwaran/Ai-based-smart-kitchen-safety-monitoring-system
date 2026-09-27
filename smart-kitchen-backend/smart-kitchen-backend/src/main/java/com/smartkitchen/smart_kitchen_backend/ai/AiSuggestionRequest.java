package com.smartkitchen.smart_kitchen_backend.ai;

import lombok.Data;

@Data
public class AiSuggestionRequest {

    private String deviceId;
    private int gasValue;
    private int flameValue;
    private int currentValue;
    private float temperature;
    private String riskLevel;
    private String alertMessage;
}