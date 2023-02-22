package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyTaskAppliedQuarterlyRepo
        extends JpaRepository<MonthlyTaskAppliedQuarterly, Long> {
    List<MonthlyTaskAppliedQuarterly> findAllByMonthlyTaskScheduler_UserId(Long id);
}
