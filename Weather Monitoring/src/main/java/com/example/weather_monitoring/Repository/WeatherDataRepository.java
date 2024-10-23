package com.example.weather_monitoring.Repository;

import com.example.weather_monitoring.Module.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findAllByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);
    WeatherData findTopByCityOrderByTimestampDesc(String city);

    List<WeatherData> findByCity(String city);
}
