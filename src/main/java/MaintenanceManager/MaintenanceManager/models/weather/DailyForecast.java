package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyForecast {

    @JsonProperty("Date")
    private String Date;

    @JsonProperty("Temperature")
    private Temperature Temperature;

    @JsonProperty("Day")
    private Day Day;

    @Override
    public String toString() {
        return "DailyForecasts{" +
                "Date='" + Date + '\'' +
                ", Temperature=" + Temperature.toString() +
                ", Day=" + Day.toString() +
                '}';
    }
}
