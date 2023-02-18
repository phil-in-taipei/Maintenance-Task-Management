package MaintenanceManager.MaintenanceManager.repositories.user;

import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMetaRepo extends JpaRepository<UserMeta, Long> {
}
