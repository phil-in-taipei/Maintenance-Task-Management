package MaintenanceManager.MaintenanceManager.services.weather;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.weather.DailyForecast;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherDataService {

    // this method exists here so that the API call to get weather forecast data is
    // only called once, if it is in the same class, the cache will not work
    // later possibly add methods to get other more specific weather data from original call
    //@Autowired
    //WeatherApiService weatherApiService;

    // this may later be used in a cronjob that reschedules Interval Task Group member
    // tasks with rainy weather restrictions on rainy days by swapping the scheduling
    // for next scheduled task in the group without a restriction

    /*
    @Loggable
    @MethodPerformance
    public Integer getRainProbability(String dateString) {
        List<DailyForecast> dailyForecasts = weatherApiService
                .getDailyWeatherForecastData(dateString);
        Integer chanceOfRain = dailyForecasts.get(0).getDay().getRainProbability();
        return chanceOfRain;
    } */
}
