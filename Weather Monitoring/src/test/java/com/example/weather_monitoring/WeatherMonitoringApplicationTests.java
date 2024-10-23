package com.example.weather_monitoring;

import com.example.weather_monitoring.DTO.AlertDTO;
import com.example.weather_monitoring.DTO.DailyWeatherSummary;
import com.example.weather_monitoring.Module.WeatherData;
import com.example.weather_monitoring.Repository.WeatherDataRepository;
import com.example.weather_monitoring.Service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WeatherMonitoringApplicationTests {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Test
    void contextLoads() {
        // Check if application context loads without errors
        assertNotNull(weatherService, "WeatherService should not be null");
        assertNotNull(weatherDataRepository, "WeatherDataRepository should not be null");
    }

    @Test
    void testFetchWeatherData() {
        // Simulate fetching weather data for all cities
        List<WeatherData> data = weatherService.fetchWeatherDataForAllCities();
        assertNotNull(data, "Weather data list should not be null");
        assertFalse(data.isEmpty(), "Weather data list should not be empty");
    }

    @Test
    void testTemperatureConversion() {
        double kelvin = 300.0;
        double expectedCelsius = 26.85;
        double actualCelsius = weatherService.kelvinToCelsius(kelvin);
        assertEquals(expectedCelsius, actualCelsius, 0.01, "Temperature conversion from Kelvin to Celsius is incorrect");
    }

    @Test
    void testDailyWeatherSummary() {
        // Assume weather data has been added for "Delhi"
        DailyWeatherSummary summary = weatherService.calculateDailySummary("Delhi");
        assertNotNull(summary, "DailyWeatherSummary should not be null");
        assertTrue(summary.getAvgTemp() > 0, "Average temperature should be greater than 0");
    }

    @Test
    void testAlertTrigger() {
        // Arrange: Set up the test data with a temperature that exceeds the threshold
        WeatherData highTempData = new WeatherData("Delhi", 35.0, 34.0, "Clear", LocalDateTime.now());
        weatherDataRepository.save(highTempData);

        // Act: Call the checkAndAlert method
        AlertDTO alert = weatherService.checkAndAlert("Delhi");

        // Assert: Check if the alert was triggered
        assertTrue(alert.isAlertTriggered(), "Expected alert to be triggered for temperature exceeding threshold");
        assertNotNull(alert.getAlertMessage(), "Alert message should not be null");
    }
}
