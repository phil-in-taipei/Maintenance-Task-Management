package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTask;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
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

    public List<MaintenanceTask> generateTaskBatchByDateListAndIntervalTaskList(
            IntervalTaskGroup intervalTaskGroup, List<LocalDate> schedulingDates
    ) {
        List<IntervalTask> intervalTasks = intervalTaskGroup.getIntervalTasks();
        int lengthOfIntervalTasks = intervalTasks.size();
        int lastIndexInIntervalTaskList = lengthOfIntervalTasks - 1;
        System.out.println("*****************Interval Tasks/length " +  lengthOfIntervalTasks + " ********************");
        System.out.println(intervalTasks.toString());
        int lengthOfDates = schedulingDates.size();
        System.out.println("*****************Dates/length " +  lengthOfDates +" *****************************");
        System.out.println(schedulingDates.toString());

        List<MaintenanceTask> batchOfTasks = new ArrayList<>();

        System.out.println("****************Now iterating through dates/tasks and matching them***********");
        System.out.println("****************While adding the tasks to a List***********");
        int indexOfIntervalTaskList = 0;
        for (LocalDate date : schedulingDates) {
            System.out.println(date + ": " + intervalTasks.get(indexOfIntervalTaskList).toString());
            IntervalTask intervalTask = intervalTasks.get(indexOfIntervalTaskList);
            MaintenanceTask maintenanceTask = new MaintenanceTask(
                    intervalTask.getIntervalTaskName(), intervalTask.getDescription(),
                    date, intervalTaskGroup.getTaskGroupOwner(), intervalTask.getNoRainOnly(),
                    intervalTaskGroup
            );
            System.out.println("*****************Maintenance task to be added*******************");
            System.out.println(maintenanceTask.toString());
            batchOfTasks.add(maintenanceTask);
            if (indexOfIntervalTaskList == lastIndexInIntervalTaskList) {
                indexOfIntervalTaskList = 0;
            } else {
                indexOfIntervalTaskList++;
            }
        }
        return batchOfTasks;
    }
}
