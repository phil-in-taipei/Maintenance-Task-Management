package MaintenanceManager.MaintenanceManager.repositories;

import MaintenanceManager.MaintenanceManager.models.UserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface UserMetaRepo extends JpaRepository<UserMeta, Long> {
}
