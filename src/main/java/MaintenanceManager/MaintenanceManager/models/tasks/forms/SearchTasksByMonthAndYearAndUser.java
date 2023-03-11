package MaintenanceManager.MaintenanceManager.models.tasks.forms;

import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Getter
@Setter
@NoArgsConstructor
public class SearchTasksByMonthAndYearAndUser {
    // this form is used to handle entry of dates as strings
    // as input in the thymeleaf template
    private Long userId;
    private Month month;
    private Integer year;
}
