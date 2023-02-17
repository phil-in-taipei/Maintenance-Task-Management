package MaintenanceManager.MaintenanceManager.repositories;

import MaintenanceManager.MaintenanceManager.models.user.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AuthorityRepo extends JpaRepository<Authority, Long> {
}
