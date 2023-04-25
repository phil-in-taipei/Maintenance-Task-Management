package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

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
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

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
        monthlyTaskAppliedQuarterlyRepo.deleteAll();
    }

    @Test
    public void testGetAllMonthlyTasksAlreadyScheduledForQuarterAndYear() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
                .id(2L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        List<MonthlyTaskAppliedQuarterly> usersMonthlyTasksAppliedQuarterly = new ArrayList<>();
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly);
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly2);
        List<MonthlyTaskScheduler> userMonthlyTaskSchedulersAppliedToTest = new ArrayList<>();
        userMonthlyTaskSchedulersAppliedToTest.add(testMonthlyTaskScheduler);
        userMonthlyTaskSchedulersAppliedToTest.add(testMonthlyTaskScheduler2);
        when(monthlyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndMonthlyTaskScheduler_UserUsername(
                        eq(QuarterlySchedulingEnum.Q2), eq(2023), anyString()))
                .thenReturn(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser")) // 1L
                .isEqualTo(userMonthlyTaskSchedulersAppliedToTest);
        assertThat(monthlyTaskSchedulingService
                .getAllMonthlyTasksAlreadyScheduledForQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser").size())
                .isEqualTo(userMonthlyTaskSchedulersAppliedToTest.size());
    }

    @Test
    public void testGetAllUsersMonthlyTasksAppliedQuarterly() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        List<MonthlyTaskAppliedQuarterly> usersMonthlyTasksAppliedQuarterly = new ArrayList<>();
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly);
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly2);
        when(monthlyTaskAppliedQuarterlyRepo
                .findAllByMonthlyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(anyString()))
                .thenReturn(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTasksAppliedQuarterly("testuser")) //1L
                .isEqualTo(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTasksAppliedQuarterly("testuser").size())
                .isEqualTo(usersMonthlyTasksAppliedQuarterly.size());
    }

    @Test
    public void testGetAllUsersMonthlyTaskSchedulers() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
                .id(2L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler2);
        when(monthlyTaskSchedulerRepo.
                findAllByUserUsernameOrderByDayOfMonthAsc(anyString()))
                .thenReturn(usersMonthlyTaskSchedulers);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers("testuser"))
                .isEqualTo(usersMonthlyTaskSchedulers);
        assertThat(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers("testuser").size())
                .isEqualTo(usersMonthlyTaskSchedulers.size());
    }

    @Test
    public void testGetUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        List<MonthlyTaskAppliedQuarterly> usersMonthlyTasksAppliedQuarterly = new ArrayList<>();
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly);
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly2);
        when(monthlyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndMonthlyTaskScheduler_UserUsername(
                        eq(QuarterlySchedulingEnum.Q2), eq(2023), anyString()))
                .thenReturn(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser"))
                .isEqualTo(usersMonthlyTasksAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .getUsersMonthlyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser").size())
                .isEqualTo(usersMonthlyTasksAppliedQuarterly.size());
    }

    // test getMonthlyTaskScheduler behavior: returns null for incorrect query id
    @Test
    public void testGetMonthlyTaskSchedulerFailureBehavior() {
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(2L))
                .isEqualTo(null);
    }

    // test getMonthlyTaskScheduler behavior for correct query id
    @Test
    public void testGetMonthlyTaskSchedulerSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        when(monthlyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(testMonthlyTaskScheduler));
        assertThat(monthlyTaskSchedulingService.getMonthlyTaskScheduler(1L))
                .isEqualTo(testMonthlyTaskScheduler);
    }

    // this tests that when a monthly task applied quarterly is
    // saved unsuccessfully an illegal argument exception is thrown
    @Test
    public void testSaveMonthlyTaskAppliedQuarterlyFailureBehavior()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler monthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
                MonthlyTaskAppliedQuarterly
                        .builder()
                        .monthlyTaskScheduler(monthlyTaskScheduler)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();

        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            monthlyTaskSchedulingService
                    .saveMonthlyTaskAppliedQuarterly(
                            testMonthlyTaskAppliedQuarterly);
        });
    }

    // test success behavior saveMonthlyTaskAppliedQuarterly returns
    // object with fields matching object which was saved
    @Test
    public void testSaveMonthlyTaskAppliedQuarterlySuccessBehavior()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MonthlyTaskScheduler monthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
                MonthlyTaskAppliedQuarterly
                .builder()
                .monthlyTaskScheduler(monthlyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        when(monthlyTaskAppliedQuarterlyRepo.save(
                any(MonthlyTaskAppliedQuarterly.class)))
                .thenReturn(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly))
                .isEqualTo(testMonthlyTaskAppliedQuarterly);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getYear())
                .isEqualTo(2023);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(monthlyTaskSchedulingService
                .saveMonthlyTaskAppliedQuarterly(
                        testMonthlyTaskAppliedQuarterly).getMonthlyTaskScheduler()
                .getMonthlyTaskName())
                .isEqualTo("Test Monthly Task Scheduler");
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
            monthlyTaskSchedulingService
                    .saveMonthlyTaskScheduler(testMonthlyTaskScheduler);
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
