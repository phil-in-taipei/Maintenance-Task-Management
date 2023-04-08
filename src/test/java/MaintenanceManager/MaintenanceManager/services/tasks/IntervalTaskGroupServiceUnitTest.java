package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskGroupRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskRepo;
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
    IntervalTaskRepo intervalTaskRepo;
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

    // rather than raising an error, this method returns null -- it is used
    // to check if an object exists prior to deletion, so this just tests
    // the expected return of a null value for query of non-existent id
    @Test
    public void testGetIntervalTaskFailureBehavior() {
        when(intervalTaskRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(intervalTaskGroupService.getIntervalTask(2L))
                .isEqualTo(null);
    }

    // test getIntervalTask behavior for correct query id
    // it is first constructed/saved as part of an interval
    // task group to mimic how it would be created/saved in the app
    @Test
    public void testGetIntervalTaskSuccessBehavior() {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        IntervalTask testIntervalTask = IntervalTask.builder()
                .id(1L)
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
        when(intervalTaskRepo.findById(anyLong()))
                .thenReturn(Optional.of(testIntervalTask));
        assertThat(intervalTaskGroupService.getIntervalTask(1L))
                .isEqualTo(testIntervalTask);
    }

    // rather than raising an error, this method returns null -- it is used
    // to check if an object exists prior to deletion, so this just tests
    // the expected return of a null value for query of non-existent id
    @Test
    public void testGetIntervalTaskGroupAppliedQuarterlyFailureBehavior()
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
                        .id(1L)
                        .intervalTaskGroup(testIntervalTaskGroup)
                        .year(2023)
                        .quarter(QuarterlySchedulingEnum.Q2)
                        .build();
        when(intervalTaskAppliedQuarterlyRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(intervalTaskGroupService
                .getIntervalTaskGroupAppliedQuarterly(2L))
                .isEqualTo(null);
    }

    // test getIntervalTaskGroupAppliedQuarterly 
    // behavior for correct query id
    @Test
    public void testGetIntervalTaskGroupAppliedQuarterlySuccessBehavior()
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
        when(intervalTaskAppliedQuarterlyRepo.findById(anyLong()))
                .thenReturn(Optional.of(testITGAQ));
        assertThat(intervalTaskGroupService.getIntervalTaskGroupAppliedQuarterly(1L))
                .isEqualTo(testITGAQ);
        assertThat(intervalTaskGroupService
                .getIntervalTaskGroupAppliedQuarterly(1L)
                .getIntervalTaskGroup().getTaskGroupName())
                .isEqualTo("Test Interval Task Group");
    }

    // rather than raising an error, this method returns null -- it is used
    // to check if an object exists prior to deletion, so this just tests
    // the expected return of a null value for query of non-existent id
    @Test
    public void testGetIntervalTaskGroupFailureBehavior() {
        when(intervalTaskGroupRepo.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThat(intervalTaskGroupService.getIntervalTaskGroup(2L))
                .isEqualTo(null);
    }

    // test getIntervalTaskGroup behavior for correct query id
    @Test
    public void testGetIntervalTaskGroupSuccessBehavior() {
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
        when(intervalTaskGroupRepo.findById(anyLong()))
                .thenReturn(Optional.of(testIntervalTaskGroup));
        assertThat(intervalTaskGroupService.getIntervalTaskGroup(1L))
                .isEqualTo(testIntervalTaskGroup);
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
