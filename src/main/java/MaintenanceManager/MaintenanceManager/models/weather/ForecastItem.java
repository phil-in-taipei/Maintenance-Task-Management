package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForecastItem {

    @JsonProperty("Date")
    private String Date;

    @JsonProperty("Temperature")
    private Temperature Temperature;

    @JsonProperty("Day")
    private Day Day;
}
