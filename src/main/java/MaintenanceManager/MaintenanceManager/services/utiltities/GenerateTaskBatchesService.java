package MaintenanceManager.MaintenanceManager.services.utiltities;
import MaintenanceManager.MaintenanceManager.logging.BatchLogger;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
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

    // this takes in a list of dates, and standard task, and a user
    // and generates the same task on each of the dates in the list
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    @MethodPerformance
    public List<MaintenanceTask> generateRecurringTasksByDateList(
            String taskName, UserPrincipal user,
            List<LocalDate> datesToScheduleTasks
    ) {
        List<MaintenanceTask> batchOfTasks = new ArrayList<>();
        for (LocalDate date : datesToScheduleTasks) {
            MaintenanceTask task = new MaintenanceTask(
                    taskName, date, user
            );
            batchOfTasks.add(task);
        }
        return batchOfTasks;
    }

    // this takes in a list of dates, and a group of alternating interval tasks
    // It iterates through the list, while also iterating through the group of interval
    // tasks and generates a batch of tasks
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    @MethodPerformance
    public List<MaintenanceTask> generateTaskBatchByDateListAndIntervalTaskList(
            IntervalTaskGroup intervalTaskGroup, List<LocalDate> schedulingDates
    ) {
        List<IntervalTask> intervalTasks = intervalTaskGroup.getIntervalTasks();
        int lengthOfIntervalTasks = intervalTasks.size();

        // this last index will be used as a switch when iterating through the sequence
        // of interval tasks. It will continue to go up by one index unless it is the
        // last index in the list of interval tasks, at which point if will reset the index
        // back to zero (and reiterate through the sequence of tasks)
        int lastIndexInIntervalTaskList = lengthOfIntervalTasks - 1;

        List<MaintenanceTask> batchOfTasks = new ArrayList<>();

        int indexOfIntervalTaskList = 0;
        for (LocalDate date : schedulingDates) {
            IntervalTask intervalTask = intervalTasks.get(indexOfIntervalTaskList);
            MaintenanceTask maintenanceTask = new MaintenanceTask(
                    intervalTask.getIntervalTaskName(), //intervalTask.getDescription(),
                    date, intervalTaskGroup.getTaskGroupOwner(), intervalTask.getNoRainOnly(),
                    intervalTaskGroup
            );

            batchOfTasks.add(maintenanceTask);
            if (indexOfIntervalTaskList == lastIndexInIntervalTaskList) {
                // the end of the interval task list has been reached,
                // so the sequence is reset back to zero
                indexOfIntervalTaskList = 0;
            } else {
                indexOfIntervalTaskList++;
            }
        }
        return batchOfTasks;
    }
}
