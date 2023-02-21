package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Wind {

    @JsonProperty("Speed")
    private Speed Speed;

    @Override
    public String toString() {
        return "Wind{" +
                "Speed=" + Speed +
                '}';
    }
}
