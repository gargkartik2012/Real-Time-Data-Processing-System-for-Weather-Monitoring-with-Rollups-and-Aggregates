package com.example.weather_monitoring.DTO;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

@AllArgsConstructor
@Getter
@Setter
public class DailyWeatherSummary {
    private String city;
    private double avgTemp;
    private double maxTemp;
    private double minTemp;
    private String dominantCondition;


}
