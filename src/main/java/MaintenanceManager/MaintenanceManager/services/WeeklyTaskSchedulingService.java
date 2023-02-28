package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskSchedulerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeeklyTaskSchedulingService {

    @Autowired
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    @Autowired
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    public List<WeeklyTaskScheduler> getAllUsersWeeklyTaskSchedulers(Long userId) {
        return weeklyTaskSchedulerRepo.findAllByUserId(userId);
    }

    public List<WeeklyTaskAppliedQuarterly>
        getAllUsersWeeklyTasksAppliedQuarterly(Long userId) {
        return weeklyTaskAppliedQuarterlyRepo.findAllByWeeklyTaskScheduler_UserId(userId);
    }

    @Transactional
    public void saveWeeklyTaskScheduler(WeeklyTaskScheduler weeklyTaskScheduler)
            throws IllegalArgumentException {
        weeklyTaskSchedulerRepo.save(weeklyTaskScheduler);
    }

    @Transactional
    public void saveWeeklyTaskAppliedQuarterly(WeeklyTaskAppliedQuarterly weeklyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        System.out.println("*****************Now saving qWeekly task in service*****************************");
        System.out.println(weeklyTaskAppliedQuarterly.toString());
        Integer year = weeklyTaskAppliedQuarterly.getYear();
        QuarterlySchedulingEnum quarter = weeklyTaskAppliedQuarterly.getQuarter();
        DayOfWeek dayOfWeek = weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getDayOfWeek();
        System.out.println("******************Now generating dates to save single tasks****************");
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getWeeklySchedulingDatesByQuarter( dayOfWeek, year, quarter);
        System.out.println(datesToScheduleTasks.toString());
        System.out.println("******************Will generate the following single tasks and add to list****************");
        List<MaintenanceTask> tasks = new ArrayList<>();
        for (LocalDate date : datesToScheduleTasks) {
            System.out.println(
                    "Task Name: " + weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getWeeklyTaskName() +
                            "; Description: " + weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getDescription() +
                            "; Local date: " + date +
                            "; User: " + weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getUser()
            );
            /*
            maintenanceTaskService.saveTask(
                    new MaintenanceTask(
                            weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getWeeklyTaskName(),
                            weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getDescription(),
                            date, weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getUser()
                    )
            );
             */
            MaintenanceTask task = new MaintenanceTask(
                    weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getWeeklyTaskName(),
                    weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getDescription(),
                    date, weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler().getUser()
            );
            tasks.add(task);
        }
        System.out.println("********************Will now save the batch of tasks************************");
        maintenanceTaskService.saveBatchOfTasks(tasks);
        System.out.println("********************Will now save the qMonthly task************************");
        weeklyTaskAppliedQuarterlyRepo.save(weeklyTaskAppliedQuarterly);
    }
}
