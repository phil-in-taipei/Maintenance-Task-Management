package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.BatchLogger;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceTaskService {
    @Autowired
    MaintenanceTaskRepo maintenanceTaskRepo;


    // This enables user to change the Task status to completed
    @Loggable
    @MethodPerformance
    @Transactional
    public void confirmTaskCompletion(MaintenanceTask task)
            throws IllegalArgumentException {
        task.setStatus(TaskStatusEnum.COMPLETED);
        maintenanceTaskRepo.save(task);
    }


    // This enables user to delete a maintenance task
    @Loggable
    @MethodPerformance
    public void deleteMaintenanceTask(Long id) {
        maintenanceTaskRepo.deleteById(id);
    }

    // gets all Maintenance tasks of a user ordered by date (used for debugging)
    @Loggable
    @MethodPerformance
    public List<MaintenanceTask> getAllUserTasks(Long userId) {

        return maintenanceTaskRepo.findAllByUserIdOrderByDateAsc(userId);
    }

    // gets all Maintenance tasks of a user between two dates
    // used to get all of user's tasks for a given month with the
    // first and last days of the month submitted as parameters
    // later could be used for weekly/quarterly/yearly search
    @Loggable
    @MethodPerformance
    public List<MaintenanceTask> getAllUserTasksInDateRange(
            Long userId, LocalDate firstDate, LocalDate lastDate) {

        return maintenanceTaskRepo
                .findAllByUserIdAndDateBetweenOrderByDateAsc(userId, firstDate, lastDate);
    }

    // gets all Maintenance Tasks scheduled for a user on a given date
    @Loggable
    @MethodPerformance
    public List<MaintenanceTask> getAllUserTasksByDate(
            Long userId, LocalDate date) {

        return maintenanceTaskRepo.findAllByUserIdAndDate(userId, date);
    }

    // gets all user tasks which have a foreign key relation with an interval task group
    // currently this is not being used, but later a cronjob may be used to swap/reschedule tasks
    // with rainy weather restriction with the next task in an Interval Task Group
    // if the weather forecast is for rain on a given day (would run prior to sending daily scheduling
    // email reminder)
    @Loggable
    @MethodPerformance
    public List<MaintenanceTask> getAllUserTasksByIntervalTaskGroup(
            Long userId, Long iTgId) {
        return maintenanceTaskRepo
                .findAllByUserIdAndIntervalTaskGroupId(userId, iTgId);
    }

    // Gets all Maintenance Tasks scheduled by a user prior to the current date
    // which have not been completed. Used for display on landing page
    @Loggable
    @MethodPerformance
    public List<MaintenanceTask> getAllUncompletedPastUserTasks(Long userId) {
        return maintenanceTaskRepo.findByStatusIsNotAndDateBeforeAndUserId(
                TaskStatusEnum.COMPLETED, LocalDate.now(), userId);
    }

    // Gets a maintenance task by id. Used to check a task exists prior to deletion and to get a
    // Maintenance task for editing (adding comments and/or rescheduling)
    @Loggable
    @MethodPerformance
    public MaintenanceTask getMaintenanceTask(Long id) {
        return maintenanceTaskRepo.findById(id)
                .orElse(null);
    }

    // Saves a List of multiple tasks in one database call. This is used when the
    // MonthlySchedulers, WeeklySchedulers, and IntervalTaskGroups are applied quarterly
    // which triggers the creation of Lists of MaintenanceTasks scheduled on dates specified
    // in the respective schedulers during the quarter/year specified in the form
    //@Loggable
    @BatchLogger // this logger is used to save resources and not print out the entire list of args
    @MethodPerformance
    @Transactional
    public void saveBatchOfTasks(List<MaintenanceTask> tasks)
            throws IllegalArgumentException {
        maintenanceTaskRepo.saveAll(tasks);
    }

    // Method to save a single task
    @Loggable
    @MethodPerformance
    @Transactional
    public void saveTask(MaintenanceTask task)
            throws IllegalArgumentException {
        maintenanceTaskRepo.save(task);
    }


}
