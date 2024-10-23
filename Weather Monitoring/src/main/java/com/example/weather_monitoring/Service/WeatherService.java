package com.example.weather_monitoring.Service;

import com.example.weather_monitoring.DTO.AlertDTO;
import com.example.weather_monitoring.DTO.DailyWeatherSummary;
import com.example.weather_monitoring.Module.WeatherData;
import com.example.weather_monitoring.Module.WeatherSummary;
import com.example.weather_monitoring.Repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherDataRepository weatherDataRepository;
    private final JavaMailSender emailSender;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final List<String> cities = Arrays.asList("Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad");
    private int consecutiveAlerts = 0;

    // Fetch weather data for all cities
    public List<WeatherData> fetchWeatherDataForAllCities() {
        RestTemplate restTemplate = new RestTemplate();
        List<WeatherData> weatherDataList = new ArrayList<>();

        for (String city : cities) {
            try {
                String url = apiUrl.replace("{CITY}", city).replace("{API_KEY}", apiKey);
                String response = restTemplate.getForObject(url, String.class);

                if (response != null) {
                    WeatherData weatherData = parseAndSaveWeatherData(response, city);
                    weatherDataList.add(weatherData);
                }
            } catch (Exception e) {
                logger.error("Error fetching weather data for city: {}", city, e);
            }
        }
        return weatherDataList;
    }

    // Parse and save weather data
    private WeatherData parseAndSaveWeatherData(String response, String city) {
        JSONObject json = new JSONObject(response);
        double tempKelvin = json.getJSONObject("main").getDouble("temp");
        double feelsLikeKelvin = json.getJSONObject("main").getDouble("feels_like");
        String mainCondition = json.getJSONArray("weather").getJSONObject(0).getString("main");

        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setTemperature(kelvinToCelsius(tempKelvin));
        weatherData.setFeelsLike(kelvinToCelsius(feelsLikeKelvin));
        weatherData.setMainCondition(mainCondition);
        weatherData.setTimestamp(LocalDateTime.now());

        weatherDataRepository.save(weatherData);
        return weatherData;
    }

    // Convert Kelvin to Celsius
    public double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    // Calculate daily weather summary
    public DailyWeatherSummary calculateDailySummary(String city) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<WeatherData> data = weatherDataRepository.findAllByCityAndTimestampBetween(city, start, end);

        if (!data.isEmpty()) {
            OptionalDouble avgTemp = data.stream().mapToDouble(WeatherData::getTemperature).average();
            double maxTemp = data.stream().mapToDouble(WeatherData::getTemperature).max().orElse(0);
            double minTemp = data.stream().mapToDouble(WeatherData::getTemperature).min().orElse(0);
            String dominantCondition = data.stream()
                    .map(WeatherData::getMainCondition)
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(condition -> condition, Collectors.counting()))
                    .entrySet().stream()
                    .max(Comparator.comparingLong(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse("No Dominant Condition");

            return new DailyWeatherSummary(city, avgTemp.orElse(0), maxTemp, minTemp, dominantCondition);
        }
        return new DailyWeatherSummary(city, 0, 0, 0, "No Data");
    }

    // Scheduled alert checker for all cities
    @Scheduled(fixedRateString = "${weather.check.interval}")
    public void checkAlertsForAllCities() {
        for (String city : cities) {
            AlertDTO alert = checkAndAlert(city);
            if (alert.isAlertTriggered()) {
                consecutiveAlerts++;
                if (consecutiveAlerts >= 2) {
                    sendEmailNotification(alert);
                    consecutiveAlerts = 0;
                }
            } else {
                consecutiveAlerts = 0;
            }
        }
    }

    // Check if alert should be triggered based on weather data
    public AlertDTO checkAndAlert(String city) {
        WeatherData latest = weatherDataRepository.findTopByCityOrderByTimestampDesc(city);
        if (latest != null && latest.getTemperature() > 34.0) {
            String message = "ALERT: Temperature exceeds 34°C in " + city + "!";
           // logger.warn(message);
            return new AlertDTO(city, true, message);
        }
        return new AlertDTO(city, false, "No alert triggered. Temperature is within safe limits.");



    }

    // Retry mechanism for email sending
    public void sendEmailNotification(AlertDTO alert) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vnighvekar0127@gmail.com"); // Replace with actual recipient email
        message.setSubject("Weather Alert for " + alert.getCity());
        message.setText(alert.getAlertMessage());

        try {
            emailSender.send(message);
            logger.info("Email sent: {}", alert.getAlertMessage());
        } catch (MailException e) {
            logger.error("Failed to send email, retrying...", e);
            // Retry logic can be implemented here
        }
    }

    public List<WeatherData> fetchWeatherDataForCity(String city) {
        // Fetch weather data for the specified city from the database
        return weatherDataRepository.findByCity(city);
    }

    public WeatherSummary getWeatherSummary(String city) {
        // Assume you fetch weather data for the city
        List<WeatherData> weatherDataList = fetchWeatherDataForCity(city);

        double totalTemperature = 0;
        double maxTemperature = Double.MIN_VALUE;
        double minTemperature = Double.MAX_VALUE;
        Map<String, Integer> conditionCount = new HashMap<>();

        for (WeatherData weatherData : weatherDataList) {
            totalTemperature += weatherData.getTemperature();
            maxTemperature = Math.max(maxTemperature, weatherData.getTemperature());
            minTemperature = Math.min(minTemperature, weatherData.getTemperature());

            // Count occurrences of each condition
            conditionCount.put(weatherData.getMainCondition(),
                    conditionCount.getOrDefault(weatherData.getMainCondition(), 0) + 1);
        }

        double averageTemperature = totalTemperature / weatherDataList.size();
        String dominantCondition = conditionCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        return new WeatherSummary(averageTemperature, maxTemperature, minTemperature, dominantCondition);
    }
}
