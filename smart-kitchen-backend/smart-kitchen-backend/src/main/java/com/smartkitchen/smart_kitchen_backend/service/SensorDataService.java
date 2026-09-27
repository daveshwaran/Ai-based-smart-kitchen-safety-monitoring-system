package com.smartkitchen.smart_kitchen_backend.service;

import com.smartkitchen.smart_kitchen_backend.entity.SensorData;
import com.smartkitchen.smart_kitchen_backend.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;

    public SensorData saveSensorData(SensorData data) {

        data.setCreatedAt(LocalDateTime.now());

        int gasValue = data.getGasValue();
        int flameValue = data.getFlameValue();
        int currentValue = data.getCurrentValue();
        float temperature = data.getTemperature();

        String riskLevel;
        String alertMessage;

        if (gasValue > 2500 && temperature > 45) {
            riskLevel = "CRITICAL";
            alertMessage = "Critical danger detected.";
        }
        else if (gasValue > 2000 && flameValue == 1) {
            riskLevel = "HIGH";
            alertMessage = "Possible gas leakage detected.";
        }
        else if (flameValue == 0 && temperature > 45) {
            riskLevel = "HIGH";
            alertMessage = "Fire risk detected.";
        }
        else if (gasValue > 1500 || temperature > 40 || currentValue > 2500) {
            riskLevel = "MEDIUM";
            alertMessage = "Warning: Sensor values abnormal.";
        }
        else {
            riskLevel = "SAFE";
            alertMessage = "Kitchen condition safe.";
        }

        data.setRiskLevel(riskLevel);
        data.setAlertMessage(alertMessage);

        return sensorDataRepository.save(data);
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public List<SensorData> getDeviceHistory(String deviceId) {
        return sensorDataRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }

    public SensorData getLatestData(String deviceId) {
        return sensorDataRepository
                .findTopByDeviceIdOrderByCreatedAtDesc(deviceId)
                .orElse(null);
    }
    public SensorData getLatestDataAnyDevice() {
        return sensorDataRepository.findTopByOrderByCreatedAtDesc()
                .orElse(null);
    }

}