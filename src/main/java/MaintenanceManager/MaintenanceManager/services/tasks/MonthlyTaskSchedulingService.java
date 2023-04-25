package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateDatesService;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonthlyTaskSchedulingService {

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @Autowired
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    // the 2 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of related monthly task scheduler models
    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteMonthlyTaskScheduler(Long id) {
        monthlyTaskSchedulerRepo
                .deleteById(id);
    }

    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteMonthlyTaskAppliedQuarterly(Long id) {
        monthlyTaskAppliedQuarterlyRepo
                .deleteById(id);
    }

    // gets all monthly task schedulers of a given user
    @Loggable
    @MethodPerformance
    public List<MonthlyTaskScheduler>
        getAllUsersMonthlyTaskSchedulers(String username) { // Long userId
            //return monthlyTaskSchedulerRepo
            //        .findAllByUserIdOrderByDayOfMonthAsc(userId);
            return monthlyTaskSchedulerRepo
                    .findAllByUserUsernameOrderByDayOfMonthAsc(username);
    }

    // gets all monthly task schedulers which have NOT already been scheduled during
    // a given quarter/year by the user. This is for the selectors in the form template
    @Loggable
    @MethodPerformance
    public List<MonthlyTaskScheduler>
        getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
            String username, QuarterlySchedulingEnum quarter, Integer year) {
        List<MonthlyTaskScheduler> allUsersMonthlyTasks =
                getAllUsersMonthlyTaskSchedulers(username); // userId
        List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks
                = getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                        quarter, year, username);
        for (MonthlyTaskScheduler aSMTS : alreadyScheduledMonthlyTasks) {
            allUsersMonthlyTasks.remove(aSMTS);
        }
        return allUsersMonthlyTasks;
    }


    // gets record of all monthly task schedulers which have been applied quarterly
    // as MonthlyTaskAppliedQuarterly objects from database
    @Loggable
    @MethodPerformance
    public List<MonthlyTaskAppliedQuarterly>
        getAllUsersMonthlyTasksAppliedQuarterly(String username) { // Long userId
            return monthlyTaskAppliedQuarterlyRepo
                .findAllByMonthlyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                        username);
    }

    // gets record of all user's MonthlyTaskAppliedQuarterly objects
    // these are records of the monthly task schedulers having been applied
    // to a given year/quarter
    @Loggable
    public List<MonthlyTaskAppliedQuarterly>
        getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear( //Long userId
            QuarterlySchedulingEnum quarter, Integer year, String username
    ) {
        return monthlyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndMonthlyTaskScheduler_UserUsername(
                quarter, year, username
        );
    }

    // gets a record of all monthly task schedulers which a
    // maintenance user has already applied for a give quarter/year
    // (they are extracted from the MonthlyTaskAppliedQuarterly objects)
    @Loggable
    @MethodPerformance
    public List<MonthlyTaskScheduler>
        getAllMonthlyTasksAlreadyScheduledForQuarterAndYear( // Long userId
            QuarterlySchedulingEnum quarter, Integer year, String username
    ) {
        List<MonthlyTaskAppliedQuarterly> qMTAQs =
                getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                quarter, year, username);
        List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks = new ArrayList<>();
        for (MonthlyTaskAppliedQuarterly mTAQ : qMTAQs) {
            alreadyScheduledMonthlyTasks.add(mTAQ.getMonthlyTaskScheduler());
        }
        return alreadyScheduledMonthlyTasks;
    }

    // queries a monthly task scheduler by id. This is make sure it
    // exists prior to deletion for error handling
    @Loggable
    @MethodPerformance
    public MonthlyTaskScheduler getMonthlyTaskScheduler(Long id) {
        return monthlyTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    // queries a MonthlyTaskAppliedQuarterly record object by id.
    // This is make sure it exists prior to deletion for error handling
    @Loggable
    @MethodPerformance
    public MonthlyTaskAppliedQuarterly getMonthlyTaskAppliedQuarterly(Long id) {
        return monthlyTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    // saves the monthly task schedulers which correspond to a set day of the month
    // return type is not void for testing
    @Loggable
    @MethodPerformance
    @Transactional
    public MonthlyTaskScheduler saveMonthlyTaskScheduler(
            MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
               return monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }

    // saves a monthly task scheduler quarterly/yearly application
    // and triggers the monthly task to be recursively scheduled on
    // the specified day of the month throughout the quarter
    @Loggable
    @MethodPerformance
    @Transactional
    public MonthlyTaskAppliedQuarterly saveMonthlyTaskAppliedQuarterly(
            MonthlyTaskAppliedQuarterly monthlyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        // Preparing to save Monthly task scheduler applied quarterly object
        // Generating dates to save single tasks on specified dates throughout quarter/year
        MonthlyTaskScheduler scheduler = monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler();
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                        monthlyTaskAppliedQuarterly.getYear(),
                        monthlyTaskAppliedQuarterly.getQuarter(),
                        scheduler.getDayOfMonth()
                );

        // Generate the following single tasks and put in List
        List<MaintenanceTask> batchOfTasks = generateTaskBatchesService
                .generateRecurringTasksByDateList(
                scheduler.getMonthlyTaskName(),
                scheduler.getUser(), datesToScheduleTasks
        );
        // Batch Save tasks in list
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        // Save the submitted quarterly applied monthly task scheduler
        return monthlyTaskAppliedQuarterlyRepo.save(monthlyTaskAppliedQuarterly);
    }
}
