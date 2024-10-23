# Real-Time-Data-Processing-System-for-Weather-Monitoring-with-Rollups-and-Aggregates
Objective
The goal of this project is to develop a real-time data processing system that continuously monitors weather conditions using the OpenWeatherMap API and provides summarized insights through rollups and aggregates.

Features
Continuous retrieval of weather data from the OpenWeatherMap API for major metros in India (Delhi, Mumbai, Chennai, Bangalore, Kolkata, Hyderabad).
Conversion of temperature values from Kelvin to Celsius based on user preferences.
Daily weather summaries including:
Average temperature
Maximum temperature
Minimum temperature
Dominant weather condition (determined based on the frequency of different weather conditions)
User-configurable alert thresholds for temperature and specific weather conditions.
Triggered alerts for breaches of defined thresholds (e.g., temperature exceeds 35°C for two consecutive updates).
Visualization of daily weather summaries, historical trends, and triggered alerts.
Data Source
Weather data is retrieved from the OpenWeatherMap API. To access the data, sign up for a free API key.

Requirements
Java (version 11 or higher)
Maven for dependency management
Spring Boot for building the application
A database (e.g., MySQL, PostgreSQL) for storing daily weather summaries
Visualization library (e.g., Chart.js, D3.js) for displaying trends and alerts

Installation
1. Clone the repository: git clone https://github.com/yourusername/weather-monitoring-system.git
cd weather-monitoring-system

2.Configure the application:
  Create a .env file or use application properties to set your OpenWeatherMap API key: OPENWEATHERMAP_API_KEY=your_api_key_here

3.Set up the database:
Create a database and configure the connection in application.properties: spring.datasource.url=jdbc:mysql://localhost:3306/weather_db
spring.datasource.username=your_username
spring.datasource.password= your_password

4.Build the application:mvn clean install

5.Run the application: mvn spring-boot:run

Usage
The application will automatically start fetching weather data at configurable intervals (e.g., every 5 minutes).
Daily summaries will be calculated and stored in the database.
If the temperature exceeds configured thresholds, alerts will be triggered and displayed in the console.
Visualizations of daily summaries and alerts can be accessed via a web interface.
API Endpoints
Here are the main endpoints for the application:

1. Weather Data Retrieval
Endpoint: GET /api/weather
Description: Fetches the latest weather data for all monitored cities.
Response: Returns the current weather conditions for the configured metros.

3. Daily Weather Summary
Endpoint: GET /api/weather/summary
Description: Retrieves the daily weather summaries.

Response: Returns average temperature, maximum temperature, minimum temperature, and dominant weather condition for each city.

5. Set Alert Thresholds
Endpoint: POST /api/weather/alerts
Description: Configures alert thresholds for temperature or specific weather conditions.

Request Body:
{
  "temperatureThreshold": 35,
  "consecutiveUpdates": 2
}

4. Triggered Alerts
Endpoint: GET /api/weather/alerts/triggered
Description: Fetches the current triggered alerts based on the latest weather data.
Response: Returns a list of alerts that have been triggered.

-> Rollups and Aggregates
Daily Weather Summary
The application aggregates the weather data daily, calculating the following:
Average Temperature: Mean of all temperature readings for the day.
Maximum Temperature: Highest temperature recorded for the day.
Minimum Temperature: Lowest temperature recorded for the day.
Dominant Weather Condition: The most frequently occurring weather condition, determined by counting occurrences of each condition.

-> Alerting Thresholds
Users can define thresholds for temperature alerts:
Example: Alert if temperature exceeds 35°C for two consecutive updates.
The application continuously checks the latest data against these thresholds and triggers alerts accordingly.

-> Visualization
The application implements visualizations to display:
Daily weather summaries.
Historical weather trends.
Triggered alerts for quick insights into weather changes.
Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes.

-> License
This project is licensed under the MIT License. See the LICENSE file for details.

-> Acknowledgments
Thanks to OpenWeatherMap for providing the weather data API.
Inspired by various data processing and visualization projects.
  
