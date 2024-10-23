package com.example.weather_monitoring.Module;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "WeatherReport")
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private double temperature;
    private double feelsLike;
    private String mainCondition;
    private LocalDateTime timestamp;

    private double minTemperature;
    private double maxTemperature;

    public WeatherData(String testCity, double v, double v1, String clear, LocalDateTime now) {
    }
}
