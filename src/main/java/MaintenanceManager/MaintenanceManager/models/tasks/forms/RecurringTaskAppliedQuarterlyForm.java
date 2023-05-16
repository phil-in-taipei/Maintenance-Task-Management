package MaintenanceManager.MaintenanceManager.models.tasks.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecurringTaskAppliedQuarterlyForm {
    
    Long recurringTaskSchedulerId;

    @Override
    public String toString() {
        return "RecurringTaskAppliedQuarterlyForm{" +
                "monthlyTaskSchedulerId=" + recurringTaskSchedulerId +
                '}';
    }
}
