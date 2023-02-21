package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Temperature implements Serializable {

    private static final long serialVersionUID = -5091239090409977427L;

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
