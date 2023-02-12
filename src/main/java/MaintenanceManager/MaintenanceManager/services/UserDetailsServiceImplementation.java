package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.repositories.UserMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {
    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Autowired
    UserMetaRepo userMetaRepo;

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );
    }
}
