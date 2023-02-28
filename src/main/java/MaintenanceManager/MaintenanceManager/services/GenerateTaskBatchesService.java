package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerateTaskBatchesService {

    public List<MaintenanceTask> generateRecurringTasksByDateList(
            String taskName, String description, UserPrincipal user,
            List<LocalDate> datesToScheduleTasks
    ) {
        List<MaintenanceTask> batchOfTasks = new ArrayList<>();
        for (LocalDate date : datesToScheduleTasks) {
            System.out.println(
                    "Task Name: " + taskName +
                            "; Description: " + description +
                            "; Local date: " + date +
                            "; User: " + user
            );
            MaintenanceTask task = new MaintenanceTask(
                    taskName, description, date, user
            );
            batchOfTasks.add(task);
        }
        return batchOfTasks;
    }
}
