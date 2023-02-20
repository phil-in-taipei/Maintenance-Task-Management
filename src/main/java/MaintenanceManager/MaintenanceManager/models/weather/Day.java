package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Day {

    private String IconPhrase;
    private String LongPhrase;
    private Integer RainProbability;
    private Wind Wind;

}
