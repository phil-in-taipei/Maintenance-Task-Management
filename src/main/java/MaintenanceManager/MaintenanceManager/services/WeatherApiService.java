package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.weather.AccuweatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    public AccuweatherResponse getDailyForecast() {
        String apiCallUrl = dailyForecastUrl + locationKey + "?apikey=" + apikey + "&details=true&metric=true";
        System.out.println("**************Calling api url: " + apiCallUrl + "  **********************");
        ResponseEntity<AccuweatherResponse> responseEntity =
                restTemplate.getForEntity(
                        apiCallUrl,
                        AccuweatherResponse.class // note: previous sample had uri variables number
                );
        //DailyForecasts results = new DailyForecasts();
        if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null) {
            AccuweatherResponse accuweatherResponse = responseEntity.getBody();
            System.out.println("******************************This is the api response********************");
            System.out.println(accuweatherResponse.getDailyForecasts().toArray()[0].toString());
            return accuweatherResponse;
        } else {
            System.out.println("Something went wrong! The response was not marked with status code 200");
            System.out.println(responseEntity.getStatusCode());
            return new AccuweatherResponse();
        }
    }

    // sample url:
    // http://dataservice.accuweather.com/forecasts/v1/daily/1day/315078?apikey=rebeahan1SXadwRERpnbQqAkGzUULepP&details=true&metric=true
}
