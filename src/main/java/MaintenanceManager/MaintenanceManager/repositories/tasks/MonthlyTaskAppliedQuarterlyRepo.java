package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyTaskAppliedQuarterlyRepo
        extends JpaRepository<MonthlyTaskAppliedQuarterly, Long> {
    List<MonthlyTaskAppliedQuarterly>
        findAllByMonthlyTaskScheduler_UserIdOrderByYearAscQuarterAsc(
                Long userId);

    List<MonthlyTaskAppliedQuarterly>
        findAllByQuarterAndYearAndMonthlyTaskScheduler_UserId( // OrderByYearAscQuarterAsc
            QuarterlySchedulingEnum quarter, Integer year, Long userId);
}
