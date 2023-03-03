package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroupAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalTaskAppliedQuarterlyRepo
        extends JpaRepository<IntervalTaskGroupAppliedQuarterly, Long> {
    List<IntervalTaskGroupAppliedQuarterly>
        findAllByIntervalTaskGroup_TaskGroupOwnerIdOrderByYearAscQuarterAsc(
                Long userId);

    List<IntervalTaskGroupAppliedQuarterly>
        findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerId(
            QuarterlySchedulingEnum quarter, Integer year, Long userId);
}
