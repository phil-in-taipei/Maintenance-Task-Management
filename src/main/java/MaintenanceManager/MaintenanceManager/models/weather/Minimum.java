package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Minimum {

    @JsonProperty("Value")
    public double Value;

    @Override
    public String toString() {
        return "Minimum{" +
                "Value=" + Value +
                '}';
    }
}
