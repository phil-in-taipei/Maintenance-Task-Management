package MaintenanceManager.MaintenanceManager.services.utilities;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTask;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateTaskBatchesService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
public class GenerateTaskBatchesServiceUnitTest {

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @MockBean
    UserDetailsServiceImplementation userService;

    @BeforeEach
    void init() {
        // creates a mock user for each test
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

    @Test
    public void testGenerateRecurringTasksByDateList() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");

        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                                "Test Task", testUser, dates)
                        .size())
                .isEqualTo(3);
        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(0).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(0).getDate()
        ).isEqualTo(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(1).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(1).getDate()
        ).isEqualTo(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(2).getTaskName()
        ).isEqualTo("Test Task");

        assertThat(
                generateTaskBatchesService.generateRecurringTasksByDateList(
                        "Test Task", testUser, dates).get(2).getDate()
        ).isEqualTo(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2023));
    }

    @Test
    public void testGenerateTaskBatchByDateListAndIntervalTaskList() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask intervalTask = IntervalTask.builder()
                .id(1L)
                .intervalTaskName("Test Task 1")
                .noRainOnly(false)
                .build();
        IntervalTask intervalTask2 = IntervalTask.builder()
                .id(2L)
                .intervalTaskName("Test Task 2")
                .noRainOnly(false)
                .build();
        IntervalTask intervalTask3 = IntervalTask.builder()
                .id(3L)
                .intervalTaskName("Test Task 3")
                .noRainOnly(false)
                .build();
        List<IntervalTask> intervalTasks = new ArrayList<>();
        intervalTasks.add(intervalTask);
        intervalTasks.add(intervalTask2);
        intervalTasks.add(intervalTask3);
        IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup.builder()
                .taskGroupName("Test interval task group")
                .intervalTasks(intervalTasks)
                .taskGroupOwner(testUser)
                .intervalInDays(5)
                .build();
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now().withMonth(4).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(5).withDayOfMonth(10).withYear(2023));
        dates.add(LocalDate.now().withMonth(6).withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .size())
                .isEqualTo(3);

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(0).getTaskName())
                .isEqualTo("Test Task 1");

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(0).getDate())
                .isEqualTo(LocalDate.now().withMonth(4)
                        .withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(1).getTaskName())
                .isEqualTo("Test Task 2");

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(1).getDate())
                .isEqualTo(LocalDate.now().withMonth(5)
                        .withDayOfMonth(10).withYear(2023));

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(2).getTaskName())
                .isEqualTo("Test Task 3");

        assertThat(
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                                testIntervalTaskGroup, dates)
                        .get(2).getDate())
                .isEqualTo(LocalDate.now().withMonth(6)
                        .withDayOfMonth(10).withYear(2023));

    }

}
