package MaintenanceManager.MaintenanceManager.models.tasks.forms;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@NoArgsConstructor
public class SearchMonthAndYear {
    private Month month;
    private Integer year;
}
