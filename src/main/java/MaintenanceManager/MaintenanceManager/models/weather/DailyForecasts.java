package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyForecasts {

    private String Date;

    private Temperature Temperature;

    private Day Day;

    @Override
    public String toString() {
        return "DailyForecasts{" +
                "Date='" + Date + '\'' +
                ", Temperature=" + Temperature +
                ", Day=" + Day +
                '}';
    }
}
