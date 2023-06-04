package MaintenanceManager.MaintenanceManager.services.users;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.*;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


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

    // this is used during registration to confirm that the
    // two passwords submitted are the same
    @Loggable
    @MethodPerformance
    public boolean confirmPasswordsMatch(UserRegistration userRegistration) {
        return Objects.equals(userRegistration.getPassword(),
                userRegistration.getPasswordConfirmation());
    }

    @Loggable
    @MethodPerformance
    public UserPrincipal createNewAdminUser(UserRegistration userRegistration) {
        // the ids of the different authorities are
        // set in the bootstrapping class
        // these are the two authorities given to admins
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
    @MethodPerformance
    public UserPrincipal createNewMaintenanceUser(UserRegistration userRegistration) {
        // the ids of the different authorities are
        // set in the bootstrapping class
        // these are the two authorities given to maintenance schedulers
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

    // gets all users who are maintenance schedulers
    // this is for the admin to view in a table of all
    // maintenance users
    @Loggable
    @MethodPerformance
    public List<UserPrincipal> getAllMaintenanceUsers() {
        return
                userPrincipalRepo.findByMaintenanceAuthority();
    }

    @Loggable
    @MethodPerformance
    public UserPrincipal getUserById(Long id) {
        return userPrincipalRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @MethodPerformance
    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );
    }

    // this is to test whether or not a username is available during registration
    // to prevent database error due to unique constraint violation
    @Loggable
    @MethodPerformance
    public boolean usernameAlreadyExists(String username) {
        try {
            UserPrincipal existentUser = loadUserByUsername(username);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
