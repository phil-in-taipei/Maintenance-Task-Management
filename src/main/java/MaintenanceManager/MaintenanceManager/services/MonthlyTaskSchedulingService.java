package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
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

    public List<MonthlyTaskScheduler> getAllUsersMonthlyTaskSchedulers(Long userId) {

        return monthlyTaskSchedulerRepo.findAllByUserId(userId);
    }

    @Transactional
    public void saveMonthlyTaskScheduler(MonthlyTaskScheduler monthlyTaskScheduler)
            throws IllegalArgumentException {
                monthlyTaskSchedulerRepo.save(monthlyTaskScheduler);
    }
}
