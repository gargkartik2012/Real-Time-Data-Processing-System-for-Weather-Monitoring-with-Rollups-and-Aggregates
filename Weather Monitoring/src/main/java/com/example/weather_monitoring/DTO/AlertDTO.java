package com.example.weather_monitoring.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Alerts")
public class AlertDTO {
    @NotBlank(message = "city can't be null")
    private String city;

    private boolean alertTriggered;
    private String alertMessage;
}