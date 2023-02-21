package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Speed implements Serializable {

    private static final long serialVersionUID = 8132442459339447828L;

    @JsonProperty("Value")
    Double Value;

    @Override
    public String toString() {
        return "Speed{" +
                "Value=" + Value +
                '}';
    }
}
