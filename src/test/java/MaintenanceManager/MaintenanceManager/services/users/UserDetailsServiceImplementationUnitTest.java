package MaintenanceManager.MaintenanceManager.services.users;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(classes= MaintenanceManagerApplication.class)
@ActiveProfiles("test-user")
@Transactional
public class UserDetailsServiceImplementationUnitTest {

    @MockBean
    UserPrincipalRepo userPrincipalRepo;

    @Autowired
    UserDetailsServiceImplementation userService;

    UserMeta userMeta = UserMeta.builder()
            .id(1L)
            .email("testuser@gmx.com")
            .surname("Test")
            .givenName("User")
            .age(50)
            .build();
    Authority userAuth = Authority.builder().id(1L)
            .authority(AuthorityEnum.ROLE_USER).build();
    Authority adminAuth = Authority.builder().id(2L)
            .authority(AuthorityEnum.ROLE_ADMIN).build();
    Authority mainAuth = Authority.builder().id(3L)
            .authority(AuthorityEnum.ROLE_MAINTENANCE).build();
    List<Authority> authoritiesForRegularUser = Arrays.asList(userAuth, mainAuth);
    List<Authority> authoritiesForAdminUser = Arrays.asList(userAuth, adminAuth);
    UserPrincipal testUser = UserPrincipal.builder()
            .id(1L)
            .enabled(true)
            .credentialsNonExpired(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .username("testuser")
            .authorities(authoritiesForRegularUser)
            .userMeta(userMeta)
            .password("testpassword")
            .build();

    UserRegistration userRegistration = UserRegistration.builder()
            .age(50)
            .email("testuser@gmx.com")
            .givenName("User")
            .surname("Test")
            .username("testuser")
            .password("testpassword")
            .passwordConfirmation("testpassword")
            .build();

    @Test
    public void testConfirmPasswordsMatch() {
        assertThat(
                userService.confirmPasswordsMatch(userRegistration))
                .isEqualTo(true);
    }

    @Test
    public void testCreateNewAdminUserSuccess() {
        testUser.setAuthorities(authoritiesForAdminUser);
        when(userPrincipalRepo.save(any(UserPrincipal.class)))
                .thenReturn(testUser);
        assertThat(userService.createNewAdminUser(userRegistration))
                .isEqualTo(testUser);
    }

    @Test
    public void testCreateNewMaintenanceUser() {
        when(userPrincipalRepo.save(any(UserPrincipal.class)))
                .thenReturn(testUser);
        assertThat(userService.createNewMaintenanceUser(userRegistration))
                .isEqualTo(testUser);
    }

    @Test
    public void testGetAllMaintenanceUsers() {
        List<UserPrincipal> allMaintenanceUsers = new ArrayList<>();
        allMaintenanceUsers.add(testUser);
        when(userPrincipalRepo
                .findByMaintenanceAuthority(
                ))
                .thenReturn((ArrayList<UserPrincipal>) allMaintenanceUsers);
        assertThat(
                userService.getAllMaintenanceUsers())
                .isEqualTo(allMaintenanceUsers);
        assertThat(
                userService.getAllMaintenanceUsers().size())
                .isEqualTo(allMaintenanceUsers.size());
    }

    @Test
    public void testGetUserByIdFailureBehavior() {
        when(userPrincipalRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(userService.getUserById(1L))
                .isEqualTo(null);
    }

    @Test
    public void testGetUserByIdSuccessBehavior() {
        when(userPrincipalRepo.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testUser));
        assertThat(userService.getUserById(1L))
                .isEqualTo(testUser);
    }

    @Test
    public void testLoadUserByUsernameFailureBehavior()
            throws UsernameNotFoundException {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("testuser");
        });
    }

    @Test
    public void testLoadUserByUsernameSuccessBehavior()
            throws UsernameNotFoundException {
        when(userPrincipalRepo.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
        assertThat(userService.loadUserByUsername("testuser"))
                .isEqualTo(testUser);
    }

    @Test
    public void testUsernameAlreadyExistsReturnsFalse() {
        assertThat(userService.usernameAlreadyExists("testuser"))
                .isEqualTo(false);
    }

    @Test
    public void testUsernameAlreadyExistsReturnsTrue() {
        when(userPrincipalRepo.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
        assertThat(userService.usernameAlreadyExists("testuser"))
                .isEqualTo(true);
    }
}
