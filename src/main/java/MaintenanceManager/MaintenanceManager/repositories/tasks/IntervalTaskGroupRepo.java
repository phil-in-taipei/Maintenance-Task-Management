package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervalTaskGroupRepo extends JpaRepository<IntervalTaskGroup, Long> {
    List<IntervalTaskGroup> findAllByTaskGroupOwnerId(Long id);

}
