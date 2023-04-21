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
    List<MaintenanceTask> findAllByUserIdOrderByDateAsc(Long id);
    List<MaintenanceTask> findAllByUserIdAndDateBetweenOrderByDateAsc(
            Long id, LocalDate firstDate, LocalDate lastDate);

    // this version is for unit test of controller -- MockUser can get UserDetails username string
    // but not the id, so querying by username, makes the controller unit testable
    List<MaintenanceTask> findAllByUserUsernameAndDateBetweenOrderByDateAsc(
            String username, LocalDate firstDate, LocalDate lastDate);
    List<MaintenanceTask> findByStatusIsNotAndDateBeforeAndUserId(
            TaskStatusEnum status, LocalDate date, Long userId);

    // this version is for unit test of controller -- MockUser can get UserDetails username string
    // but not the id, so querying by username, makes the controller unit testable
    List<MaintenanceTask> findByStatusIsNotAndDateBeforeAndUserUsername(
            TaskStatusEnum status, LocalDate date, String username);

    List<MaintenanceTask> findAllByUserIdAndDate(Long id, LocalDate date);

    // this version is for unit test of controller -- MockUser can get UserDetails username string
    // but not the id, so querying by username, makes the controller unit testable
    List<MaintenanceTask> findAllByUserUsernameAndDate(String id, LocalDate date);

    // modify to after date
    List<MaintenanceTask> findAllByUserIdAndIntervalTaskGroupId(Long userId, Long iTgId);
}
