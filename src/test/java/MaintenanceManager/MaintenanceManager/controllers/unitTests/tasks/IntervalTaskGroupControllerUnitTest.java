package MaintenanceManager.MaintenanceManager.controllers.unitTests.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.tasks.IntervalTaskGroupController;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.IntervalTaskGroupService;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IntervalTaskGroupController.class)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class IntervalTaskGroupControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    IntervalTaskGroupService intervalTaskGroupService;

    @MockBean
    MaintenanceTaskService maintenanceTaskService;

    @MockBean
    UserDetailsServiceImplementation userService;

    UserMeta userMeta = UserMeta.builder()
            .id(1L)
            .email("testuser@gmx.com")
            .surname("Test")
            .givenName("User")
            .age(50)
            .build();
    Authority authority1 = Authority.builder().authority(
            AuthorityEnum.ROLE_USER).build();
    Authority authority2 = Authority.builder().authority(
            AuthorityEnum.ROLE_MAINTENANCE).build();
    List<Authority> authorities = Arrays.asList(authority1, authority2);
    UserPrincipal testUser = UserPrincipal.builder()
            .id(1L)
            .enabled(true)
            .credentialsNonExpired(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .username("testuser")
            .authorities(authorities)
            .userMeta(userMeta)
            .password("testpassword")
            .build();

    IntervalTask testIntervalTask = IntervalTask.builder()
            .id(1L)
            .intervalTaskName("Test Interval Task 1")
            .noRainOnly(false)
            .build();

    IntervalTask testIntervalTask2 = IntervalTask.builder()
            .id(2L)
            .intervalTaskName("Test Interval Task 2")
            .noRainOnly(false)
            .build();

    IntervalTaskGroup testIntervalTaskGroup = IntervalTaskGroup.builder()
            .id(1L)
            .taskGroupName("Test Interval Task Group 1")
            .intervalInDays(3)
            .taskGroupOwner(testUser)
            .build();

    IntervalTaskGroup testIntervalTaskGroup2 = IntervalTaskGroup.builder()
            .id(2L)
            .taskGroupName("Test Interval Task Group 2")
            .intervalInDays(3)
            .taskGroupOwner(testUser)
            .build();

    IntervalTaskGroupAppliedQuarterly testITGAQ1 = IntervalTaskGroupAppliedQuarterly
            .builder()
            .id(1L)
            .intervalTaskGroup(testIntervalTaskGroup)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    IntervalTaskGroupAppliedQuarterly testITGAQ2 = IntervalTaskGroupAppliedQuarterly
            .builder()
            .id(2L)
            .intervalTaskGroup(testIntervalTaskGroup2)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserIntervalTaskGroups() throws Exception {
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        List<IntervalTaskGroup> testIntervalTaskGroups = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        testIntervalTasks.add(testIntervalTask2);
        testIntervalTaskGroup.setIntervalTasks(testIntervalTasks);
        testIntervalTaskGroups.add(testIntervalTaskGroup);
        testIntervalTaskGroups.add(testIntervalTaskGroup2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService.getAllUsersIntervalTaskGroups(
                anyString()))
                .thenReturn(testIntervalTaskGroups);
        mockMvc
                .perform(get("/interval-task-groups"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("intervalTaskGroups"))
                .andExpect(model().attributeExists("intervalTaskQuarterAndYear"))
                .andExpect(model().attributeExists("user"))
                // these substrings are the titles of the task groups and quarterly/years
                // applications created above, so it makes sure that the expected
                // objects are displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task Group 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task Group 2")))
                .andExpect(view().name("tasks/interval-task-groups"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserQuarterlyIntervalTaskGroups() throws Exception {
        List<IntervalTaskGroupAppliedQuarterly> usersITGAQs = new ArrayList<>();
        usersITGAQs.add(testITGAQ1);
        usersITGAQs.add(testITGAQ2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService.getAllUsersIntervalTaskGroupsAppliedQuarterly(
                anyString()))
                .thenReturn(usersITGAQs);
        mockMvc
                .perform(get("/quarterly-interval-task-groups-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qITG"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created above
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task Group 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task Group 2")))
                .andExpect(view().name(
                        "tasks/quarterly-interval-task-groups-scheduled"));
    }
}
