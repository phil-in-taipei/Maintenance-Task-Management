package MaintenanceManager.MaintenanceManager.repositories;
import MaintenanceManager.MaintenanceManager.models.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface MaintenanceTaskRepo extends JpaRepository<MaintenanceTask, Long>{
    List<MaintenanceTask> findAllByUserId(Long id);
}
