package MaintenanceManager.MaintenanceManager.repositories.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EnumType;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceTaskRepo extends JpaRepository<MaintenanceTask, Long> {
    List<MaintenanceTask> findAllByUserId(Long id);

    List<MaintenanceTask> findByStatusIsNotAndDateBefore(TaskStatusEnum status, LocalDate date);

    List<MaintenanceTask> findAllByUserIdAndDate(Long id, LocalDate date);
}