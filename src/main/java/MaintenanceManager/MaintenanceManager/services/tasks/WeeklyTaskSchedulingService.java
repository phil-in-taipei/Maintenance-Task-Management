package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateDatesService;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    public List<WeeklyTaskScheduler> getAllUsersWeeklyTaskSchedulers(Long userId) {
        return weeklyTaskSchedulerRepo.findAllByUserIdOrderByDayOfWeekAsc(userId);
    }

    public List<WeeklyTaskAppliedQuarterly>
        getAllUsersWeeklyTasksAppliedQuarterly(Long userId) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByWeeklyTaskScheduler_UserIdOrderByYearAscQuarterAsc(userId);
    }

    public List<WeeklyTaskScheduler>
        getAllUsersWeeklyTaskSchedulersAvailableForQuarterAndYear(
            Long userId, QuarterlySchedulingEnum quarter, Integer year) {
        List<WeeklyTaskScheduler> allUsersWeeklyTasks =
                getAllUsersWeeklyTaskSchedulers(userId);
        List<WeeklyTaskScheduler> alreadyScheduledWeeklyTasks
                = getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
                quarter, year, userId);
        for (WeeklyTaskScheduler aSWTS : alreadyScheduledWeeklyTasks) {
            allUsersWeeklyTasks.remove(aSWTS);
        }
        return allUsersWeeklyTasks;
    }

    public List<WeeklyTaskScheduler>
     getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, Long userId
    ) {
        List<WeeklyTaskAppliedQuarterly> qWTAQs =
                getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        quarter, year, userId);
        List<WeeklyTaskScheduler> alreadyScheduledWeeklyTasks = new ArrayList<>();
        for (WeeklyTaskAppliedQuarterly wTAQ : qWTAQs) {
            alreadyScheduledWeeklyTasks.add(wTAQ.getWeeklyTaskScheduler());
        }
        return alreadyScheduledWeeklyTasks;
    }

    public List<WeeklyTaskAppliedQuarterly>
        getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, Long userId
    ) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserId( //OrderByYearAscQuarterAsc
                        quarter, year, userId
                );
    }

    @Transactional
    public void saveWeeklyTaskScheduler(WeeklyTaskScheduler weeklyTaskScheduler)
            throws IllegalArgumentException {
        weeklyTaskSchedulerRepo.save(weeklyTaskScheduler);
    }

    @Transactional
    public void saveWeeklyTaskAppliedQuarterly(WeeklyTaskAppliedQuarterly weeklyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        WeeklyTaskScheduler scheduler = weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler();

        System.out.println("*****************Now saving qWeekly task in service*****************************");
        System.out.println(weeklyTaskAppliedQuarterly.toString());

        System.out.println("******************Now generating dates to save single tasks****************");
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getWeeklySchedulingDatesByQuarter(
                        scheduler.getDayOfWeek(),
                        weeklyTaskAppliedQuarterly.getYear(),
                        weeklyTaskAppliedQuarterly.getQuarter()
                );
        System.out.println(datesToScheduleTasks.toString());
        System.out.println("******************Will generate the following single tasks and add to list****************");

        List<MaintenanceTask> batchOfTasks = generateTaskBatchesService.generateRecurringTasksByDateList(
                scheduler.getWeeklyTaskName(),
                scheduler.getDescription(),
                scheduler.getUser(), datesToScheduleTasks
        );
        System.out.println("********************Will now save the batch of tasks************************");
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        System.out.println("********************Will now save the qMonthly task************************");
        weeklyTaskAppliedQuarterlyRepo.save(weeklyTaskAppliedQuarterly);
    }
}
