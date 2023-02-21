package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AccuweatherResponse {

    @JsonProperty("DailyForecasts")
    private List<DailyForecast> DailyForecasts;
}
