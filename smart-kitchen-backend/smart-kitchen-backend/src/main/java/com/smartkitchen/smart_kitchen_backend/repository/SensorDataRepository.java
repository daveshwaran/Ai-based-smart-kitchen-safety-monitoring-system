package com.smartkitchen.smart_kitchen_backend.repository;

import com.smartkitchen.smart_kitchen_backend.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findByDeviceIdOrderByCreatedAtDesc(String deviceId);

    Optional<SensorData> findTopByDeviceIdOrderByCreatedAtDesc(String deviceId);

    Optional<SensorData> findTopByOrderByCreatedAtDesc();
}