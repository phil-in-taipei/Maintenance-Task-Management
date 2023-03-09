package MaintenanceManager.MaintenanceManager.services.users;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.models.user.*;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserPrincipalRepo userPrincipalRepo;

    @Autowired
    AuthorityRepo authorityRepo;

    @Autowired
    UserMetaRepo userMetaRepo;

    @Loggable
    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );
    }

    @Loggable
    public UserPrincipal createNewAdminUser(UserRegistration userRegistration) {
        Authority userAuth = authorityRepo.getById(1L);
        Authority mainAuth = authorityRepo.getById(2L);
        UserMeta userMeta = UserMeta.builder()
                .surname(userRegistration.getSurname())
                .givenName(userRegistration.getGivenName())
                .email(userRegistration.getEmail())
                .age(userRegistration.getAge())
                .build();
        UserPrincipal newUser = new UserPrincipal(userRegistration.getUsername(),
                passwordEncoder.encode(userRegistration.getPassword()),
                Arrays.asList(userAuth, mainAuth), userMeta);
        return userPrincipalRepo.save(newUser);
    }

    @Loggable
    public UserPrincipal createNewMaintenanceUser(UserRegistration userRegistration) {
        Authority userAuth = authorityRepo.getById(1L);
        Authority mainAuth = authorityRepo.getById(3L);
        UserMeta userMeta = UserMeta.builder()
                .surname(userRegistration.getSurname())
                .givenName(userRegistration.getGivenName())
                .email(userRegistration.getEmail())
                .age(userRegistration.getAge())
                .build();
        UserPrincipal newUser = new UserPrincipal(userRegistration.getUsername(),
                passwordEncoder.encode(userRegistration.getPassword()),
        									Arrays.asList(userAuth, mainAuth), userMeta);
        return userPrincipalRepo.save(newUser);
    }

    @Loggable
    public List<UserPrincipal> getAllMaintenanceUsers() {
        return userPrincipalRepo.findByMaintenanceAuthority();
    }
}
