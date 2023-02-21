package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Speed {

    @JsonProperty("Value")
    Double Value;

    @Override
    public String toString() {
        return "Speed{" +
                "Value=" + Value +
                '}';
    }
}
