package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Temperature {

    @JsonProperty("Minimum")
    public Minimum Minimum;

    @JsonProperty("Maximum")
    public Maximum Maximum;

    @Override
    public String toString() {
        return "Temperature{" +
                "Minimum=" + Minimum.toString() +
                ", Maximum=" + Maximum.toString() +
                '}';
    }
}
