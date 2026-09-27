package com.smartkitchen.smart_kitchen_backend.ai;

import com.smartkitchen.smart_kitchen_backend.entity.SensorData;
import com.smartkitchen.smart_kitchen_backend.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AiSuggestionController {

    private final AiSuggestionService aiSuggestionService;
    private final SensorDataService sensorDataService;

    @GetMapping("/suggestion/latest")
    public AiSuggestionResponse getLatestSuggestion() {

        SensorData latestData = sensorDataService.getLatestDataAnyDevice();

        if (latestData == null) {
            return new AiSuggestionResponse("No sensor data available.");
        }

        String suggestion = aiSuggestionService.generateSuggestion(latestData);

        return new AiSuggestionResponse(suggestion);
    }
}