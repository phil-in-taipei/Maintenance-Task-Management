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

    public List<MonthlyTaskScheduler> getAllUsersMonthlyTaskSchedulers(Long userId) {
        return monthlyTaskSchedulerRepo.findAllByUserId(userId);
    }

    @Transactional
    public void saveMonthlyTaskScheduler(MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
                monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }

    @Transactional
    public void saveMonthlyTaskAppliedQuarterly(MonthlyTaskAppliedQuarterly monthlyTaskAppliedQuarterly)
            throws IllegalArgumentException {
        //MonthlyTaskAppliedQuarterly newObj = monthlyTaskAppliedQuarterlyRepo.save(monthlyTaskAppliedQuarterly);
        //Integer year = newObj.getYear();
        //QuarterlySchedulingEnum quarter = newObj.getQuarter();
        //Integer dayOfMonth = newObj.getMonthlyTaskScheduler().getDayOfMonth();
        Integer year = monthlyTaskAppliedQuarterly.getYear();
        QuarterlySchedulingEnum quarter = monthlyTaskAppliedQuarterly.getQuarter();
        Integer dayOfMonth = monthlyTaskAppliedQuarterly.getMonthlyTaskScheduler().getDayOfMonth();
        List<LocalDate> datesToScheduleTasks =
                generateDatesService.getMonthlySchedulingDatesByQuarter(year, quarter, dayOfMonth);
        System.out.println(datesToScheduleTasks.toString());
    }
}
