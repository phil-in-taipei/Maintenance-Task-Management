package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
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

    @Loggable
    @Transactional
    public void deleteMonthlyTaskScheduler(Long id) {
        monthlyTaskSchedulerRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteMonthlyTaskAppliedQuarterly(Long id) {
        monthlyTaskAppliedQuarterlyRepo.deleteById(id);
    }

    @Loggable
    public List<MonthlyTaskScheduler> getAllUsersMonthlyTaskSchedulers(Long userId) {
        return monthlyTaskSchedulerRepo.findAllByUserIdOrderByDayOfMonthAsc(userId);
    }

    @Loggable
    public List<MonthlyTaskScheduler>
        getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
            Long userId, QuarterlySchedulingEnum quarter, Integer year) {
        List<MonthlyTaskScheduler> allUsersMonthlyTasks =
                getAllUsersMonthlyTaskSchedulers(userId);
        List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks
                = getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                        quarter, year, userId);
        for (MonthlyTaskScheduler aSMTS : alreadyScheduledMonthlyTasks) {
            allUsersMonthlyTasks.remove(aSMTS);
        }
        return allUsersMonthlyTasks;
    }

    @Loggable
    public List<MonthlyTaskAppliedQuarterly>
        getAllUsersMonthlyTasksAppliedQuarterly(Long userId) {
            return monthlyTaskAppliedQuarterlyRepo
                .findAllByMonthlyTaskScheduler_UserIdOrderByYearAscQuarterAsc(
                        userId);
    }

    @Loggable
    public List<MonthlyTaskAppliedQuarterly>
        getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, Long userId
    ) {
        return monthlyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndMonthlyTaskScheduler_UserId(
                quarter, year, userId
        );
    }

    @Loggable
    public List<MonthlyTaskScheduler>
        getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
            QuarterlySchedulingEnum quarter, Integer year, Long userId
    ) {
        List<MonthlyTaskAppliedQuarterly> qMTAQs =
                getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                quarter, year, userId);
        List<MonthlyTaskScheduler> alreadyScheduledMonthlyTasks = new ArrayList<>();
        for (MonthlyTaskAppliedQuarterly mTAQ : qMTAQs) {
            alreadyScheduledMonthlyTasks.add(mTAQ.getMonthlyTaskScheduler());
        }
        return alreadyScheduledMonthlyTasks;
    }

    @Loggable
    public MonthlyTaskScheduler getMonthlyTaskScheduler(Long id) {
        return monthlyTaskSchedulerRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    public MonthlyTaskAppliedQuarterly getMonthlyTaskAppliedQuarterly(Long id) {
        return monthlyTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @Transactional
    public void saveMonthlyTaskScheduler(MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
                monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }

    @Loggable
    @Transactional
    public void saveMonthlyTaskAppliedQuarterly(MonthlyTaskAppliedQuarterly monthlyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        System.out.println("*****************Now saving qMonthly task in service*****************************");
        System.out.println(monthlyTaskAppliedQuarterly.toString());
        System.out.println("******************Now generating dates to save single tasks****************");
        MonthlyTaskScheduler scheduler = monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler();
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getMonthlySchedulingDatesByQuarter(
                        monthlyTaskAppliedQuarterly.getYear(),
                        monthlyTaskAppliedQuarterly.getQuarter(),
                        scheduler.getDayOfMonth()
                );
        System.out.println(datesToScheduleTasks.toString());
        System.out.println("******************Will generate the following single tasks and put in List****************");
        List<MaintenanceTask> batchOfTasks = generateTaskBatchesService
                .generateRecurringTasksByDateList(
                scheduler.getMonthlyTaskName(), scheduler.getDescription(),
                scheduler.getUser(), datesToScheduleTasks
        );
        System.out.println("********************Batch Save tasks in list************************");
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        System.out.println("********************Will now save the qMonthly task************************");
        monthlyTaskAppliedQuarterlyRepo.save(monthlyTaskAppliedQuarterly);
    }
}
