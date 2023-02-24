package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTaskSchedulerRepo extends JpaRepository<WeeklyTaskScheduler, Long> {
    List<WeeklyTaskScheduler> findAllByUserId(Long id);
}
