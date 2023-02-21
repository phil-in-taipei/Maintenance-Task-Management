package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Minimum implements Serializable {

    private static final long serialVersionUID = -643909702186140265L;

    @JsonProperty("Value")
    public double Value;

    @Override
    public String toString() {
        return "Minimum{" +
                "Value=" + Value +
                '}';
    }
}
