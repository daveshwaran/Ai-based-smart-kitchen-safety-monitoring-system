package com.smartkitchen.smart_kitchen_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private int gasValue;

    private int flameValue;

    private int currentValue;

    private float temperature;

    private String riskLevel;

    private String alertMessage;

    private LocalDateTime createdAt;
}