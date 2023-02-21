package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Maximum implements Serializable{

    private static final long serialVersionUID = 40937949161343408L;

    @Override
    public String toString() {
        return "Maximum{" +
                "Value=" + Value +
                '}';
    }

    @JsonProperty("Value")
    public double Value;
}
