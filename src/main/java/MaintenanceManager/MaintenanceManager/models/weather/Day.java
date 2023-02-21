package MaintenanceManager.MaintenanceManager.models.weather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Day implements Serializable {

    private static final long serialVersionUID = 9071825978230879739L;

    @JsonProperty("IconPhrase")
    private String IconPhrase;

    @JsonProperty("LongPhrase")
    private String LongPhrase;

    @JsonProperty("RainProbability")
    private Integer RainProbability;

    @JsonProperty("Wind")
    private Wind Wind;

    @Override
    public String toString() {
        return "Day{" +
                "IconPhrase='" + IconPhrase + '\'' +
                ", LongPhrase='" + LongPhrase + '\'' +
                ", RainProbability=" + RainProbability +
                ", Wind=" + Wind.toString() +
                '}';
    }
}
