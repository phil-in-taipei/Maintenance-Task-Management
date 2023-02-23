package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    MaintenanceTaskService maintenanceTaskService;

    public List<MonthlyTaskScheduler> getAllUsersMonthlyTaskSchedulers(Long userId) {
        return monthlyTaskSchedulerRepo.findAllByUserId(userId);
    }

    public List<MonthlyTaskAppliedQuarterly> getAllUsersMonthlyTasksAppliedQuarterly(Long userId) {
        return monthlyTaskAppliedQuarterlyRepo.findAllByMonthlyTaskScheduler_UserId(userId);
    }

    @Transactional
    public void saveMonthlyTaskScheduler(MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
                monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }

    @Transactional
    public void saveMonthlyTaskAppliedQuarterly(MonthlyTaskAppliedQuarterly monthlyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        System.out.println("*****************Now saving qMonthly task in service*****************************");
        System.out.println(monthlyTaskAppliedQuarterly.toString());
        Integer year = monthlyTaskAppliedQuarterly.getYear();
        QuarterlySchedulingEnum quarter = monthlyTaskAppliedQuarterly.getQuarter();
        Integer dayOfMonth = monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getDayOfMonth();
        System.out.println("******************Now generating dates to save single tasks****************");
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getMonthlySchedulingDatesByQuarter(year, quarter, dayOfMonth);
        System.out.println(datesToScheduleTasks.toString());
        System.out.println("******************Will save the following single tasks****************");
        for (LocalDate date : datesToScheduleTasks) {
            System.out.println(
                    "Task Name: " + monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getMonthlyTaskName() +
                    "; Description: " + monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getDescription() +
                    "; Local date: " + date +
                    "; User: " + monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getUser()
            );
            maintenanceTaskService.saveTask(
                    new MaintenanceTask(
                            monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getMonthlyTaskName(),
                            monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getDescription(),
                            date, monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getUser()
                            )
            );
        }
        System.out.println("********************Will now save the qMonthly task************************");
        monthlyTaskAppliedQuarterlyRepo.save(monthlyTaskAppliedQuarterly);
    }
}
