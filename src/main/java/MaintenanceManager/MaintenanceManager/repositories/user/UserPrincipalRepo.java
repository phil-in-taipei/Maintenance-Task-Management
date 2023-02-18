package MaintenanceManager.MaintenanceManager.repositories.user;

import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserPrincipalRepo extends JpaRepository<UserPrincipal, Long> {
    Optional<UserPrincipal> findByUsername(String username);

    // note: this will only work as long as the id for the maintenance role in db is 3!!!
    @Query(value = "SELECT * FROM users u INNER JOIN user_authority_join_table ua ON u.id = ua.user_id "
            + "INNER JOIN authorities a ON ua.authority_id = a.id WHERE a.id = 3", nativeQuery = true)
    ArrayList<UserPrincipal> findByMaintenanceAuthority();

}
