package MaintenanceManager.MaintenanceManager.models.tasks.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaintenanceTaskSubmit {

    private String taskName;
    private String description;
    private String date;
}
