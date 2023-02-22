package MaintenanceManager.MaintenanceManager.models.tasks.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyTaskSubmit {
    private String monthlyTaskName;
    private String description;
    private Integer dayOfMonth;
}
