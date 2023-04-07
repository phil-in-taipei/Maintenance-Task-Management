package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskGroupRepo;
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
public class IntervalTaskGroupServiceUnitTest {

    @MockBean
    MaintenanceTaskRepo maintenanceTaskRepo;

    @MockBean
    IntervalTaskAppliedQuarterlyRepo intervalTaskAppliedQuarterlyRepo;

    @MockBean
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;

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
        intervalTaskAppliedQuarterlyRepo.deleteAll();
        intervalTaskGroupRepo.deleteAll();
    }

    // tests throwing an error in case the interval task group does not save properly
    @Test
    public void testSaveIntervalTaskGroupFailureError() throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask testIntervalTask = IntervalTask.builder()
                .intervalTaskName("Test Interval Task")
                .noRainOnly(false)
                .build();
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup
                .builder()
                .intervalInDays(3)
                .intervalTasks(testIntervalTasks)
                .taskGroupName("Test Interval Task Group")
                .taskGroupOwner(testUser)
                .build();
        when(intervalTaskGroupRepo.save(any(IntervalTaskGroup.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            intervalTaskGroupService
                    .saveIntervalTaskGroup(testIntervalTaskGroup);
        });

    }

    // tests that the interval task group is saved with the correct template
    // selector string indicating the number of days for the interval
    // also tests that the relational interval task object saves
    @Test
    public void testSaveIntervalTaskGroup() throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask testIntervalTask = IntervalTask.builder()
                .intervalTaskName("Test Interval Task")
                .noRainOnly(false)
                .build();
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup
                .builder()
                .id(1L)
                .intervalInDays(3)
                .intervalTasks(testIntervalTasks)
                .taskGroupName("Test Interval Task Group")
                .taskGroupOwner(testUser)
                .build();
        when(intervalTaskGroupRepo.save(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroup(testIntervalTaskGroup))
                .isEqualTo(testIntervalTaskGroup);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroup(testIntervalTaskGroup).templateSelector())
                .isEqualTo("Test Interval Task Group (Every 3 days)");
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroup(testIntervalTaskGroup).getIntervalTasks().size())
                .isEqualTo(1);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroup(testIntervalTaskGroup).getIntervalTasks().get(0))
                .isEqualTo(testIntervalTask);
    }

    // tests that the interval task group is saved with the correct string
    // for name of relational interval task group object and the correct quarter/year
    @Test
    public void testSaveIntervalTaskGroupAppliedQuarterly()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask testIntervalTask = IntervalTask.builder()
                .intervalTaskName("Test Interval Task")
                .noRainOnly(false)
                .build();
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup
                .builder()
                .id(1L)
                .intervalInDays(3)
                .intervalTasks(testIntervalTasks)
                .taskGroupName("Test Interval Task Group")
                .taskGroupOwner(testUser)
                .build();
        IntervalTaskGroupAppliedQuarterly testITGAQ =
                IntervalTaskGroupAppliedQuarterly.builder()
                        .intervalTaskGroup(testIntervalTaskGroup)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        when(intervalTaskAppliedQuarterlyRepo.save(
                any(IntervalTaskGroupAppliedQuarterly.class)))
                .thenReturn(testITGAQ);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        testITGAQ))
                .isEqualTo(testITGAQ);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        testITGAQ).getYear())
                .isEqualTo(2023);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        testITGAQ).getQuarter())
                .isEqualTo(QuarterlySchedulingEnum.Q2);
        assertThat(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        testITGAQ).getIntervalTaskGroup().getTaskGroupName())
                .isEqualTo("Test Interval Task Group");
    }

    // tests throwing an error in case the interval task group
    // applied quarterly does not save properly
    @Test
    public void testSaveIntervalTaskGroupAppliedQuarterlyFailureError()
            throws IllegalArgumentException {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask testIntervalTask = IntervalTask.builder()
                .intervalTaskName("Test Interval Task")
                .noRainOnly(false)
                .build();
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup
                .builder()
                .id(1L)
                .intervalInDays(3)
                .intervalTasks(testIntervalTasks)
                .taskGroupName("Test Interval Task Group")
                .taskGroupOwner(testUser)
                .build();
        IntervalTaskGroupAppliedQuarterly testITGAQ =
                IntervalTaskGroupAppliedQuarterly.builder()
                        .intervalTaskGroup(testIntervalTaskGroup)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        when(intervalTaskAppliedQuarterlyRepo.save(
                any(IntervalTaskGroupAppliedQuarterly.class)))
                .thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> {
            intervalTaskGroupService
                    .saveIntervalTaskGroupAppliedQuarterly(
                            testITGAQ);
        });
    }
}
