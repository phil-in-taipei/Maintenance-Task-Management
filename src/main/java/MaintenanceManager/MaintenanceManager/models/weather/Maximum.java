package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Maximum {

    @Override
    public String toString() {
        return "Maximum{" +
                "Value=" + Value +
                '}';
    }

    @JsonProperty("Value")
    public double Value;
}
