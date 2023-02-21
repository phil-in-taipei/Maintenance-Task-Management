package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DailyForecast implements Serializable{

    private static final long serialVersionUID = 645945833607401129L;

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
