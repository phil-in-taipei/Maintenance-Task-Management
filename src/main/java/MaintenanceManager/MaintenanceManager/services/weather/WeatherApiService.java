package MaintenanceManager.MaintenanceManager.services.weather;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.weather.AccuweatherResponse;
import MaintenanceManager.MaintenanceManager.models.weather.DailyForecast;
import org.springframework.cache.annotation.Cacheable;
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
public class WeatherApiService {
    @Value("${api_key}")
    private String apikey;

    @Value("${dailyForecastUrl}")
    private String dailyForecastUrl;

    @Value("${api_location_key}")
    private String locationKey;

    @Autowired
    RestTemplate restTemplate;

    @Loggable
    @MethodPerformance
    public AccuweatherResponse fetchDailyForecastEntity() {
        String apiCallUrl = dailyForecastUrl + locationKey + "?apikey=" +
                apikey + "&details=true&metric=true";
        //System.out.println("**************Calling api url: " + apiCallUrl + "  **********************");
        ResponseEntity<AccuweatherResponse> responseEntity =
                restTemplate.getForEntity(
                        apiCallUrl,
                        AccuweatherResponse.class // note: previous sample had uri variables number
                );
        //System.out.println("*************API Call Time: " + LocalDateTime.now() + "*************************");
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)
                && responseEntity.getBody() != null) {
            AccuweatherResponse accuweatherResponse = responseEntity.getBody();
            //System.out.println("******************************This is the api response********************");
            //System.out.println(accuweatherResponse.getDailyForecasts().toArray()[0].toString());
            return accuweatherResponse;
        } else {
            //System.out.println("Something went wrong! The response was not marked with status code 200");
            //System.out.println(responseEntity.getStatusCode());
            return new AccuweatherResponse();
        }
    }

    @Loggable
    @MethodPerformance
    @Cacheable(
            value = "dailyWeatherCache",
            key = "#dateString")
    public List<DailyForecast> getDailyWeatherForecastData(String dateString) {
        //System.out.println("*************Cached Method called at: " + dateString + "*****************");
        return fetchDailyForecastEntity().getDailyForecasts();
    }

}
