package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Wind implements Serializable {

    private static final long serialVersionUID = -2250110866653639909L;

    @JsonProperty("Speed")
    private Speed Speed;

    @Override
    public String toString() {
        return "Wind{" +
                "Speed=" + Speed +
                '}';
    }
}
