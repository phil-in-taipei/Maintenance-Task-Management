package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskScheduler;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskSchedulerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
}
