package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
public class MonthlyTaskSchedulingServiceUnitTest {

    @MockBean
    MaintenanceTaskRepo maintenanceTaskRepo;

    @MockBean
    MaintenanceTaskService maintenanceTaskService;

    @MockBean
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @MockBean
    UserDetailsServiceImplementation userService;

    @BeforeEach
    void init() {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setSurname( "User");
        userRegistration.setGivenName( "Test");
        userRegistration.setEmail("test@gmx.com");
        userRegistration.setAge(40);
        userRegistration.setUsername("testuser");
        userRegistration.setPassword("testpassword");
        userRegistration.setPasswordConfirmation("testpassword");
        userService.createNewMaintenanceUser(userRegistration);
    }

    @AfterEach
    void clearMockRepos() {
        maintenanceTaskRepo.deleteAll();
        monthlyTaskSchedulerRepo.deleteAll();
    }

    // this tests that when a monthly task scheduler is saved unsuccessfully
    // an illegal argument exception is thrown
    @Test
    public void testSaveMonthlyTaskSchedulerFailureError()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.save(any(MonthlyTaskScheduler.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            monthlyTaskSchedulingService.saveMonthlyTaskScheduler(testMonthlyTaskScheduler);
        });
    }

    // this tests that monthly task scheduler is saved and that
    // the template string displays a proper ordinal number for day
    // of the month ending in '1'
    @Test
    public void testSaveMonthlyTaskSchedulerFirstOrdinal()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.save(any(MonthlyTaskScheduler.class)))
                .thenReturn(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler))
                .isEqualTo(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler).templateSelector())
                .isEqualTo("Test Monthly Task Scheduler: 1st day of month ");

    }

    // this tests that monthly task scheduler is saved and that
    // the template string displays a proper ordinal number for day
    // of the month
    @Test
    public void testSaveMonthlyTaskSchedulerRegularOrdinal()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(5)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.save(any(MonthlyTaskScheduler.class)))
                .thenReturn(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler))
                .isEqualTo(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler).templateSelector())
                .isEqualTo("Test Monthly Task Scheduler: 5th day of month ");

    }

    // this tests that monthly task scheduler is saved and that
    // the template string displays a proper ordinal number for day
    // of the month ending in '2'
    @Test
    public void testSaveMonthlyTaskSchedulerSecondOrdinal()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(2)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.save(any(MonthlyTaskScheduler.class)))
                .thenReturn(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler))
                .isEqualTo(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler).templateSelector())
                .isEqualTo("Test Monthly Task Scheduler: 2nd day of month ");

    }

    // this tests that monthly task scheduler is saved and that
    // the template string displays a proper ordinal number for day
    // of the month ending in '3'
    @Test
    public void testSaveMonthlyTaskSchedulerThirdOrdinal()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(3)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.save(any(MonthlyTaskScheduler.class)))
                .thenReturn(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler))
                .isEqualTo(testMonthlyTaskScheduler);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskScheduler(testMonthlyTaskScheduler).templateSelector())
                .isEqualTo("Test Monthly Task Scheduler: 3rd day of month ");

    }
}
