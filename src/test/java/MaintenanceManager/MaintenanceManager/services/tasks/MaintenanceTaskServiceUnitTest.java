package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
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
public class MaintenanceTaskServiceUnitTest {

    @MockBean
    MaintenanceTaskRepo maintenanceTaskRepo;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

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
    void clearMockRepo() {
        maintenanceTaskRepo.deleteAll();
    }

    @Test
    public void testGetAllUncompletedPastUserTasksSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now().minusDays(1))
                .user(testUser)
                .build();
        MaintenanceTask maintenanceTask2 = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 2")
                .date(LocalDate.now().minusDays(2))
                .user(testUser)
                .build();
        List<MaintenanceTask> tasksOnPreviousDates = new ArrayList<>();
        tasksOnPreviousDates.add(maintenanceTask);
        tasksOnPreviousDates.add(maintenanceTask2);
        when(
                maintenanceTaskRepo
                        .findByStatusIsNotAndDateBeforeAndUserId(
                                eq(TaskStatusEnum.COMPLETED),
                                eq(LocalDate.now()), anyLong())
        ).thenReturn(tasksOnPreviousDates);
        assertThat(
                maintenanceTaskService.getAllUncompletedPastUserTasks(
                        1L))
                .isEqualTo(tasksOnPreviousDates);
        assertThat(
                maintenanceTaskService.getAllUncompletedPastUserTasks(1L).size())
                .isEqualTo(tasksOnPreviousDates.size());
    }

    @Test
    public void testGetAllUserTasksByDateSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now())
                .user(testUser)
                .build();
        MaintenanceTask maintenanceTask2 = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 2")
                .date(LocalDate.now())
                .user(testUser)
                .build();
        List<MaintenanceTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(maintenanceTask);
        tasksOnCurrentDate.add(maintenanceTask2);
        when(
                maintenanceTaskRepo.findAllByUserIdAndDate(anyLong(),  eq(LocalDate.now()))
        ).thenReturn(tasksOnCurrentDate);
        assertThat(
                maintenanceTaskService.getAllUserTasksByDate(1L, LocalDate.now()))
                .isEqualTo(tasksOnCurrentDate);
        assertThat(
                maintenanceTaskService.getAllUserTasksByDate(1L, LocalDate.now()).size())
                .isEqualTo(tasksOnCurrentDate.size());
    }

    @Test
    public void testGetAllAllUserTasksInDateRangeSuccess() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfThisMonth = firstDayOfThisMonth.plusMonths(1)
                .minusDays(1);
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(firstDayOfThisMonth.plusDays(2))
                .user(testUser)
                .build();
        MaintenanceTask maintenanceTask2 = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 2")
                .date(firstDayOfThisMonth.plusDays(3))
                .user(testUser)
                .build();
        List<MaintenanceTask> tasksInCurrentMonth = new ArrayList<>();
        tasksInCurrentMonth.add(maintenanceTask);
        tasksInCurrentMonth.add(maintenanceTask2);
        when(
                maintenanceTaskRepo.findAllByUserIdAndDateBetweenOrderByDateAsc(
                        anyLong(),  eq(firstDayOfThisMonth), eq(lastDayOfThisMonth))
        ).thenReturn(tasksInCurrentMonth);
        assertThat(
                maintenanceTaskService
                        .getAllUserTasksInDateRange(1L, firstDayOfThisMonth,
                                lastDayOfThisMonth))
                .isEqualTo(tasksInCurrentMonth);
        assertThat(
                maintenanceTaskService
                        .getAllUserTasksInDateRange(1L, firstDayOfThisMonth,
                lastDayOfThisMonth).size())
                .isEqualTo(tasksInCurrentMonth.size());
    }


    @Test
    public void testGetMaintenanceTaskSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now())
                .user(testUser)
                .build();
        when(maintenanceTaskRepo.findById(anyLong()))
                .thenReturn(Optional.of(maintenanceTask));
        assertThat(maintenanceTaskService.getMaintenanceTask(1L))
                .isEqualTo(maintenanceTask);
    }

    @Test
    public void testGetMaintenanceTaskFailureBehavior() {
        // this does not return an error (returns null if no task exists with the id)
        when(maintenanceTaskRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(maintenanceTaskService.getMaintenanceTask(2L))
                .isEqualTo(null);
    }

    @Test
    public void testSaveMaintenanceTaskSuccessBehavior()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now())
                .user(testUser)
                .build();
        when(maintenanceTaskRepo.save(any(MaintenanceTask.class)))
                .thenReturn(maintenanceTask);
        assertThat(maintenanceTaskService.saveTask(maintenanceTask))
                .isEqualTo(maintenanceTask);
    }

    // this tests that when a maintenance task is saved unsuccessfully
    // an illegal argument exception is thrown
    @Test
    public void testSaveMaintenanceTaskFailureBehavior() throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now())
                .user(testUser)
                .build();
        when(maintenanceTaskRepo.save(any(MaintenanceTask.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            maintenanceTaskService.saveTask(maintenanceTask);
        });
    }

}

