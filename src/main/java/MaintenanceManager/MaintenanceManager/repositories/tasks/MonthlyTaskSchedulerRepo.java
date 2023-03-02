package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyTaskSchedulerRepo extends JpaRepository<MonthlyTaskScheduler, Long> {
    List<MonthlyTaskScheduler> findAllByUserIdOrderByDayOfMonthAsc(Long id);

    List<MonthlyTaskScheduler> findAllByUserIdAndMonthlyTaskAppliedQuarterly_QuarterNotAndMonthlyTaskAppliedQuarterly_YearNot(
            Long id, QuarterlySchedulingEnum quarter, Integer year); // query name of the year?
}
// findAllByMonthlyTaskScheduler_UserIdOrderByYearAscQuarterAsc