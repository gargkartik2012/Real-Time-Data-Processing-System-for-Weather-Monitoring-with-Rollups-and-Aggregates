package com.example.weather_monitoring;

import com.example.weather_monitoring.DTO.AlertDTO;
import com.example.weather_monitoring.DTO.DailyWeatherSummary;
import com.example.weather_monitoring.Module.WeatherData;
import com.example.weather_monitoring.Repository.WeatherDataRepository;
import com.example.weather_monitoring.Service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherDataRepository weatherDataRepository;


    @Mock
    private JavaMailSender emailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckAndAlert_HighTemperature() {
        // Arrange
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setTemperature(35.0); // Set temperature above alert threshold
        when(weatherDataRepository.findTopByCityOrderByTimestampDesc("Delhi")).thenReturn(mockWeatherData);

        // Act
        AlertDTO alert = weatherService.checkAndAlert("Delhi");

        // Assert
        assertTrue(alert.isAlertTriggered());
        assertEquals("ALERT: Temperature exceeds 34°C in Delhi!", alert.getAlertMessage());
    }

    @Test
    public void testCheckAndAlert_NormalTemperature() {
        // Arrange
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setTemperature(30.0); // Set temperature below alert threshold
        when(weatherDataRepository.findTopByCityOrderByTimestampDesc("Delhi")).thenReturn(mockWeatherData);

        // Act
        AlertDTO alert = weatherService.checkAndAlert("Delhi");

        // Assert
        assertFalse(alert.isAlertTriggered());
        assertEquals("No alert triggered. Temperature is within safe limits.", alert.getAlertMessage());
    }

    @Test
    public void testSendEmailNotification() {
        // Arrange
        AlertDTO alert = new AlertDTO("Delhi", true, "ALERT: Temperature exceeds 34°C in Delhi!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vnighvekar0127@gmail.com");
        message.setSubject("Weather Alert for Delhi");
        message.setText(alert.getAlertMessage());

        // Act
        weatherService.sendEmailNotification(alert);

        // Assert
        verify(emailSender, times(1)).send(message);
    }
    @Test
    public void testCalculateDailySummary() {
        // Arrange
        WeatherData mockWeatherData1 = new WeatherData();
        mockWeatherData1.setCity("Delhi");
        mockWeatherData1.setTemperature(30.0);
        mockWeatherData1.setMainCondition("Clear");
        mockWeatherData1.setTimestamp(LocalDateTime.now());

        WeatherData mockWeatherData2 = new WeatherData();
        mockWeatherData2.setCity("Delhi");
        mockWeatherData2.setTemperature(32.0);
        mockWeatherData2.setMainCondition("Clear");
        mockWeatherData2.setTimestamp(LocalDateTime.now());

        when(weatherDataRepository.findAllByCityAndTimestampBetween(eq("Delhi"), any(), any()))
                .thenReturn(Arrays.asList(mockWeatherData1, mockWeatherData2));

        // Act
        DailyWeatherSummary summary = weatherService.calculateDailySummary("Delhi");

        // Assert
        assertNotNull(summary);
        assertEquals("Delhi", summary.getCity());
        assertEquals(31.0, summary.getAvgTemp());
        assertEquals(32.0, summary.getMaxTemp());
        assertEquals(30.0, summary.getMinTemp());
        assertEquals("Clear", summary.getDominantCondition());
    }


    @Test
    public void testFetchWeatherDataForAllCities() {
        // Arrange
        when(weatherDataRepository.save(any(WeatherData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<WeatherData> weatherDataList = weatherService.fetchWeatherDataForAllCities();

        // Assert
        assertNotNull(weatherDataList);
        //assertFalse(weatherDataList.isEmpty());
        // Additional assertions can be made based on expected values
    }

    // Test for temperature conversion
    @Test
    public void testKelvinToCelsius() {
        // Arrange
        double kelvin = 300.0;

        // Act
        double celsius = weatherService.kelvinToCelsius(kelvin);

        // Assert
        assertEquals(26.85, celsius, 0.01); // 300K should equal approximately 26.85C
    }

    @Test
    public void testCalculateDailySummary_NoData() {
        // Arrange
        when(weatherDataRepository.findAllByCityAndTimestampBetween(eq("Delhi"), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        DailyWeatherSummary summary = weatherService.calculateDailySummary("Delhi");

        // Assert
        assertNotNull(summary);
        assertEquals("Delhi", summary.getCity());
        assertEquals(0, summary.getAvgTemp());
        assertEquals(0, summary.getMaxTemp());
        assertEquals(0, summary.getMinTemp());
        assertEquals("No Data", summary.getDominantCondition());
    }
}