package MaintenanceManager.MaintenanceManager.models.tasks.forms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchTasksByDate {
    // this form is used to handle entry of dates as strings
    // as input in the thymeleaf template
    private String date;
}
