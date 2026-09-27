package com.smartkitchen.smart_kitchen_backend.controller;

import com.smartkitchen.smart_kitchen_backend.entity.SensorData;
import com.smartkitchen.smart_kitchen_backend.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @PostMapping("/add")
    public SensorData addSensorData(@RequestBody SensorData data) {
        return sensorDataService.saveSensorData(data);
    }

    @GetMapping("/all")
    public List<SensorData> getAllSensorData() {
        return sensorDataService.getAllSensorData();
    }

    @GetMapping("/history/{deviceId}")
    public List<SensorData> getDeviceHistory(@PathVariable String deviceId) {
        return sensorDataService.getDeviceHistory(deviceId);
    }

    @GetMapping("/latest/{deviceId}")
    public SensorData getLatestData(@PathVariable String deviceId) {
        return sensorDataService.getLatestData(deviceId);
    }
    @GetMapping("/latest")
    public SensorData getLatestDataAnyDevice() {
        return sensorDataService.getLatestDataAnyDevice();
    }
}