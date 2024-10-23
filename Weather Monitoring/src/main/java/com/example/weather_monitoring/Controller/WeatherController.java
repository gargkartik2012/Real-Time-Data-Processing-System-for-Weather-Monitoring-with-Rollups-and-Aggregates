package com.example.weather_monitoring.Controller;

import com.example.weather_monitoring.DTO.AlertDTO;
import com.example.weather_monitoring.DTO.DailyWeatherSummary;
import com.example.weather_monitoring.Module.WeatherData;
import com.example.weather_monitoring.Module.WeatherSummary;
import com.example.weather_monitoring.Service.WeatherService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {


    @Autowired
    private WeatherService weatherService;


        // Fetch weather data for all cities
        @GetMapping("/fetch")
        public ResponseEntity<List<WeatherData>> fetchWeatherDataForAllCities() {
            List<WeatherData> weatherDataList = weatherService.fetchWeatherDataForAllCities();
            if (weatherDataList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(weatherDataList);
        }

        // Fetch daily summary for a specific city
    @GetMapping("/summary")
    public ResponseEntity<String> getWeatherSummary(@RequestParam(value = "city", required = false) String city, Model model) {
        if (city == null || city.isEmpty()) {
            return new ResponseEntity<>("City name is required.", HttpStatus.BAD_REQUEST);
        }
        DailyWeatherSummary summary = weatherService.calculateDailySummary(city);
        if (summary == null) {
            return new ResponseEntity<>("No summary available for the specified city.", HttpStatus.NOT_FOUND);
        }
        model.addAttribute("summary", summary);
        model.addAttribute("city", city);
        return new ResponseEntity<>("Summary loaded successfully.", HttpStatus.OK);
    }

    // Check weather alert for a specific city
    @GetMapping("/alert")
    public ResponseEntity<String> checkWeatherAlert(@RequestParam(value = "city", required = false) String city, Model model) {
        if (city == null || city.isEmpty()) {
            return new ResponseEntity<>("City name is required.", HttpStatus.BAD_REQUEST);
        }
        AlertDTO alert = weatherService.checkAndAlert(city);
        if (alert == null) {
            return new ResponseEntity<>("No alert data available for the specified city.", HttpStatus.NOT_FOUND);
        }
        model.addAttribute("alert", alert);
        model.addAttribute("city", city);
        return new ResponseEntity<>("Alert loaded successfully.", HttpStatus.OK);
    }



    // Fetch daily summary of weather for a specific city
    @GetMapping("/summary/{city}")
    public ResponseEntity<DailyWeatherSummary> calculateDailySummary(@PathVariable String city) {
        DailyWeatherSummary summary = weatherService.calculateDailySummary(city);
        if (summary == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(summary);
    }

    // Fetch weather alert for a specific city
    @GetMapping("/alert/{city}")
    public ResponseEntity<AlertDTO> checkWeatherAlert(
            @PathVariable @Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid city name") String city) {
        AlertDTO alert = weatherService.checkAndAlert(city);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/weather/view")
    public String viewWeatherData(Model model) {
        List<WeatherData> weatherDataList = weatherService.fetchWeatherDataForAllCities();
        model.addAttribute("weatherDataList", weatherDataList);
        return "weather"; // Return the name of the Thymeleaf template
    }


}
