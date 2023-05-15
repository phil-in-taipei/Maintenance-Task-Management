package MaintenanceManager.MaintenanceManager.models.tasks.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyClassAppliedQuarterlyForm {
    
    Long monthlyTaskSchedulerId;

    @Override
    public String toString() {
        return "MonthlyClassAppliedQuarterlyForm{" +
                "monthlyTaskSchedulerId=" + monthlyTaskSchedulerId +
                '}';
    }
}
