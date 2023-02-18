package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.user.*;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserMetaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
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

    public UserPrincipal createNewUser(UserRegistration userRegistration) {
        Authority userAuth = Authority.builder().authority(AuthorityEnum.ROLE_USER).build();
        Authority mainAuth = Authority.builder().authority(AuthorityEnum.ROLE_MAINTENANCE).build();
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
}
