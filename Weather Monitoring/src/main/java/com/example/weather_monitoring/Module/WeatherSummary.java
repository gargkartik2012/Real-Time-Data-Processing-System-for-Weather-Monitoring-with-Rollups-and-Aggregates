package com.example.weather_monitoring.Module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherSummary {
    private double averageTemperature; // Average temperature
    private double maxTemperature;      // Maximum temperature
    private double minTemperature;      // Minimum temperature
    private String dominantCondition;   // Dominant weather condition
}
