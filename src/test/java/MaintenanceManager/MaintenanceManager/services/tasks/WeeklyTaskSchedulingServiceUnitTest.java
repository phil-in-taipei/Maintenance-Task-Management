package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
public class WeeklyTaskSchedulingServiceUnitTest {

    @MockBean
    MaintenanceTaskRepo maintenanceTaskRepo;

    @MockBean
    UserDetailsServiceImplementation userService;

    @MockBean
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @MockBean
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    @Autowired
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

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
        weeklyTaskAppliedQuarterlyRepo.deleteAll();
        weeklyTaskSchedulerRepo.deleteAll();
    }

    @Test
    public void testGetAllWeeklyTasksAlreadyScheduledForQuarterAndYear() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        WeeklyTaskScheduler weeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler 2")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler2)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        List<WeeklyTaskScheduler> userWeeklyTaskSchedulersAppliedToTest = new ArrayList<>();
        userWeeklyTaskSchedulersAppliedToTest.add(weeklyTaskScheduler);
        userWeeklyTaskSchedulersAppliedToTest.add(weeklyTaskScheduler2);
        when(weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserUsername(
                        eq(QuarterlySchedulingEnum.Q2), eq(2023), anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser"))
                .isEqualTo(userWeeklyTaskSchedulersAppliedToTest);
        assertThat(weeklyTaskSchedulingService
                .getAllWeeklyTasksAlreadyScheduledForQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser").size())
                .isEqualTo(userWeeklyTaskSchedulersAppliedToTest.size());
    }

    @Test
    public void testGetAllUsersWeeklyTasksAppliedQuarterly() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        WeeklyTaskScheduler weeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
                .id(2L)
                .weeklyTaskName("Test Weekly Task Scheduler 2")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler2)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        when(weeklyTaskAppliedQuarterlyRepo
                .findAllByWeeklyTaskScheduler_UserUsernameOrderByYearAscQuarterAsc(
                        anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTasksAppliedQuarterly("testuser"))
                .isEqualTo(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTasksAppliedQuarterly("testuser").size())
                .isEqualTo(usersWeeklyTasksAppliedQuarterly.size());
    }

    @Test
    public void testGetAllUsersWeeklyTaskSchedulers() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskScheduler weeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
                .id(2L)
                .weeklyTaskName("Test Weekly Task Scheduler 2")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .user(testUser)
                .build();
        List<WeeklyTaskScheduler> userWeeklyTaskSchedulers = new ArrayList<>();
        userWeeklyTaskSchedulers.add(weeklyTaskScheduler);
        userWeeklyTaskSchedulers.add(weeklyTaskScheduler2);
        when(weeklyTaskSchedulerRepo.findAllByUserUsernameOrderByDayOfWeekAsc(anyString()))
                .thenReturn(userWeeklyTaskSchedulers);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTaskSchedulers("testuser"))
                .isEqualTo(userWeeklyTaskSchedulers);
        assertThat(weeklyTaskSchedulingService
                .getAllUsersWeeklyTaskSchedulers("testuser").size())
                .isEqualTo(userWeeklyTaskSchedulers.size());
    }

    @Test
    public void testGetUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        WeeklyTaskScheduler weeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
                .id(2L)
                .weeklyTaskName("Test Weekly Task Scheduler 2")
                .dayOfWeek(DayOfWeek.TUESDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler2)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        when(weeklyTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndWeeklyTaskScheduler_UserUsername(
                        eq(QuarterlySchedulingEnum.Q2), eq(2023), anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser"))
                .isEqualTo(usersWeeklyTasksAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .getUsersWeeklyTasksAppliedQuarterlyByQuarterAndYear(
                        QuarterlySchedulingEnum.Q2, 2023, "testuser").size())
                .isEqualTo(usersWeeklyTasksAppliedQuarterly.size());
    }

    // test getWeeklyTaskScheduler behavior: returns null for incorrect query id
    @Test
    public void testGetWeeklyTaskSchedulerFailureBehavior() {
        when(weeklyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskScheduler(2L))
                .isEqualTo(null);
    }

    // test getWeeklyTaskScheduler behavior for correct query id
    @Test
    public void testGetWeeklyTaskSchedulerSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler testWeeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        when(weeklyTaskSchedulerRepo.findById(anyLong()))
                .thenReturn(Optional.of(testWeeklyTaskScheduler));
        assertThat(weeklyTaskSchedulingService.getWeeklyTaskScheduler(1L))
                .isEqualTo(testWeeklyTaskScheduler);
    }

    // this tests that when a weekly task applied quarterly is
    // saved unsuccessfully an illegal argument exception is thrown
    @Test
    public void testSaveWeeklyTaskAppliedQuarterlyFailureBehavior()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        when(weeklyTaskAppliedQuarterlyRepo.save(
                any(WeeklyTaskAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            weeklyTaskSchedulingService
                    .saveWeeklyTaskAppliedQuarterly(
                            testWeeklyTaskAppliedQuarterly
                    );
        });
    }

    // test success behavior saveWeeklyTaskAppliedQuarterly returns
    // object with fields matching object which was saved
    @Test
    public void testSaveMonthlyTaskAppliedQuarterlySuccessBehavior()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler weeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly
                = WeeklyTaskAppliedQuarterly
                .builder()
                .weeklyTaskScheduler(weeklyTaskScheduler)
                .year(2023)
                .quarter(QuarterlySchedulingEnum.Q2)
                .build();
        when(weeklyTaskAppliedQuarterlyRepo.save(
                any(WeeklyTaskAppliedQuarterly.class)))
                .thenReturn(testWeeklyTaskAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly))
                .isEqualTo(testWeeklyTaskAppliedQuarterly);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly).getYear())
                .isEqualTo(2023);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskAppliedQuarterly(
                        testWeeklyTaskAppliedQuarterly)
                .getWeeklyTaskScheduler().getWeeklyTaskName())
                .isEqualTo("Test Weekly Task Scheduler");
    }

    // this tests that when a weekly task scheduler is saved unsuccessfully
    // an illegal argument exception is thrown
    @Test
    public void testSaveWeeklyTaskSchedulerFailureError()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        WeeklyTaskScheduler testWeeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        when(weeklyTaskSchedulerRepo.save(any(WeeklyTaskScheduler.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            weeklyTaskSchedulingService
                    .saveWeeklyTaskScheduler(testWeeklyTaskScheduler);
        });
    }

    // this tests that weekly task scheduler is saved and that
    // the template string displays a proper day of the week
    @Test
    public void testSaveWeeklyTaskSchedulerTemplateSelector()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        // weeklyTaskName + " (every " + dayOfWeekStr + ")"
        WeeklyTaskScheduler testWeeklyTaskScheduler = WeeklyTaskScheduler.builder()
                .id(1L)
                .weeklyTaskName("Test Weekly Task Scheduler")
                .dayOfWeek(DayOfWeek.MONDAY)
                .user(testUser)
                .build();
        when(weeklyTaskSchedulerRepo.save(any(WeeklyTaskScheduler.class)))
                .thenReturn(testWeeklyTaskScheduler);
        assertThat(weeklyTaskSchedulingService
                .saveWeeklyTaskScheduler(testWeeklyTaskScheduler).templateSelector())
                .isEqualTo("Test Weekly Task Scheduler (every Monday)");
    }
}
