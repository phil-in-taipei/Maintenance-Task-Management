package MaintenanceManager.MaintenanceManager.repositories;
import MaintenanceManager.MaintenanceManager.models.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface MaintenanceTaskRepo extends JpaRepository<MaintenanceTask, Long>{

}
