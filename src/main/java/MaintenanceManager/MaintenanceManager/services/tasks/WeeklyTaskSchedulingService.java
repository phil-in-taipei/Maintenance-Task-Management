package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
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


    // the 2 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of related weekly task scheduler models
    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteWeeklyTaskScheduler(Long id) {
        weeklyTaskSchedulerRepo.deleteById(id);
    }

    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteWeeklyTaskAppliedQuarterly(Long id) {
        weeklyTaskAppliedQuarterlyRepo.deleteById(id);
    }

    // gets all weekly task schedulers of a given user
    @Loggable
    @MethodPerformance
    public List<WeeklyTaskScheduler> getAllUsersWeeklyTaskSchedulers(Long userId) {
        return weeklyTaskSchedulerRepo.findAllByUserIdOrderByDayOfWeekAsc(userId);
    }

    // gets record of all weekly task schedulers which have been applied quarterly
    // as WeeklyTaskAppliedQuarterly objects from database
    @Loggable
    @MethodPerformance
    public List<WeeklyTaskAppliedQuarterly>
        getAllUsersWeeklyTasksAppliedQuarterly(Long userId) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByWeeklyTaskScheduler_UserIdOrderByYearAscQuarterAsc(userId);
    }

    // gets all weekly task schedulers which have NOT already been scheduled during
    // a given quarter/year by the user. This is for the selectors in the form template
    @Loggable
    @MethodPerformance
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

    // gets a record of all weekly task schedulers which a
    // maintenance user has already applied for a give quarter/year
    // (they are extracted from the WeeklyTaskAppliedQuarterly objects)
    @Loggable
    @MethodPerformance
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

    // gets record of all user's WeeklyTaskAppliedQuarterly objects
    // these are records of the weekly task schedulers having been applied
    // to a given year/quarter
    @Loggable
    @MethodPerformance
    public List<WeeklyTaskAppliedQuarterly>
        getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, Long userId
    ) {
        return weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserId(
                        quarter, year, userId
                );
    }

    // queries a weekly task scheduler by id. This is make sure it
    // exists prior to deletion for error handling
    @Loggable
    @MethodPerformance
    public WeeklyTaskScheduler getWeeklyTaskScheduler(Long id) {
        return weeklyTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    // queries a WeeklyTaskAppliedQuarterly record object by id.
    // This is make sure it exists prior to deletion for error handling
    @Loggable
    @MethodPerformance
    public WeeklyTaskAppliedQuarterly getWeeklyTaskAppliedQuarterly(Long id) {
        return weeklyTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    // saves the weekly task schedulers which correspond to a set day of the week
    @Loggable
    @MethodPerformance
    @Transactional
    public WeeklyTaskScheduler saveWeeklyTaskScheduler(WeeklyTaskScheduler weeklyTaskScheduler)
            throws IllegalArgumentException {
        return weeklyTaskSchedulerRepo.save(weeklyTaskScheduler);
    }

    // saves a weekly task scheduler quarterly/yearly application
    // and triggers the weekly task to be recursively scheduled on
    // the specified day of the week throughout the quarter
    @Loggable
    @MethodPerformance
    @Transactional
    public WeeklyTaskAppliedQuarterly saveWeeklyTaskAppliedQuarterly(
            WeeklyTaskAppliedQuarterly weeklyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        WeeklyTaskScheduler scheduler = weeklyTaskAppliedQuarterly.getWeeklyTaskScheduler();

        // Preparing to save quarterly-applied weekly task scheduler
        System.out.println(weeklyTaskAppliedQuarterly.toString());

        // Generating dates to save single tasks on each of the specified days in the quarter/year
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getWeeklySchedulingDatesByQuarter(
                        scheduler.getDayOfWeek(),
                        weeklyTaskAppliedQuarterly.getYear(),
                        weeklyTaskAppliedQuarterly.getQuarter()
                );

        // Generate the single tasks on the dates in the datesToScheduleTasks List
        List<MaintenanceTask> batchOfTasks = generateTaskBatchesService
                .generateRecurringTasksByDateList(
                    scheduler.getWeeklyTaskName(),
                    scheduler.getUser(), datesToScheduleTasks
        );

        // Saves the batch of generated tasks for the quarter/year
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        // Saves the quarterly-applied weekly task scheduler
        return weeklyTaskAppliedQuarterlyRepo.save(weeklyTaskAppliedQuarterly);
    }
}
