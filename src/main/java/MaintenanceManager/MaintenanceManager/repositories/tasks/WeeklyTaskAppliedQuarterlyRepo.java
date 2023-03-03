package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskAppliedQuarterly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTaskAppliedQuarterlyRepo
        extends JpaRepository<WeeklyTaskAppliedQuarterly, Long> {
    List<WeeklyTaskAppliedQuarterly>
        findAllByWeeklyTaskScheduler_UserIdOrderByYearAscQuarterAsc(
            Long userId);

    List<WeeklyTaskAppliedQuarterly>
        findAllByQuarterAndYearAndWeeklyTaskScheduler_UserId( // OrderByYearAscQuarterAsc
            QuarterlySchedulingEnum quarter, Integer year, Long userId);
}
