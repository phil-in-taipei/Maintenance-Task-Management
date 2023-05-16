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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void testDeleteIntervalTask() throws Exception {
        when(intervalTaskGroupService
                .getIntervalTask(anyLong()))
                .thenReturn(testIntervalTask);
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup);
        mockMvc.
                perform(request(HttpMethod.GET, "/delete-interval-task/"
                        + testIntervalTask.getId() + "/" + testIntervalTaskGroup.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-group/"
                        + testIntervalTaskGroup.getId()));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteIntervalTaskFailure() throws Exception {
        Long nonExistentIntervalTaskID= 2829L;
        Long nonExistentIntervalTaskGroupID = 333L;
        String message1 = "Cannot delete, ";
        String message2 = " does not exist.";
        when(intervalTaskGroupService
                .getIntervalTask(anyLong()))
                .thenReturn(null);
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET, "/delete-interval-task/"
                        + testIntervalTask.getId() + "/" + testIntervalTaskGroup.getId()))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message1)))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message2)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteIntervalTaskGroup() throws Exception {
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup);
        mockMvc.
                perform(request(HttpMethod.GET, "/delete-interval-task-group/"
                        + testIntervalTaskGroup.getId()))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-groups"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteIntervalTaskGroupFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, interval task group with id: "
                + nonExistentID + " does not exist";
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-interval-task-group/"
                                + nonExistentID))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteIntervalTaskGroupAppliedQuarterly() throws Exception {
        when(intervalTaskGroupService
                .getIntervalTaskGroupAppliedQuarterly(anyLong()))
                .thenReturn(testITGAQ1);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-interval-task-group-applied-quarterly/"
                                + testITGAQ1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/quarterly-interval-task-groups-scheduled"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteIntervalTaskGroupAppliedQuarterlyFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, interval task group applied quarterly with id: "
                + nonExistentID + " does not exist";
        when(intervalTaskGroupService
                .getIntervalTaskGroupAppliedQuarterly(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-interval-task-group-applied-quarterly/"
                                + nonExistentID))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    // test to save an interval task, which will be a member of an interval task
    // group created above. The id of the interval task group will be used as the
    // path variable in the post request
    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewIntervalTask() throws Exception {
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup);
        List<IntervalTask> intervalTasks = new ArrayList<>();
        intervalTasks.add(testIntervalTask);
        testIntervalTaskGroup.setIntervalTasks(intervalTasks);
        when(intervalTaskGroupService
                .saveIntervalTaskGroup(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup);
        MockHttpServletRequestBuilder createIntervalTask = post(
                "/interval-task-group/" + testIntervalTaskGroup.getId())
                .with(csrf())
                .param("noRainOnly", String.valueOf(false))
                .param("intervalTaskName", "Test Interval Task 1");
        mockMvc.perform(createIntervalTask)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-groups"));
    }

    // test to save an interval task, which will be a member of a non-existent
    // interval task group. Because the path variable is the id of a group that does
    // not exist, saving should fail, and it should return an error page
    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewIntervalTaskFailure() throws Exception {
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(null);
        Long nonExistentIntervalTaskGroupID = 2829L;
        String message = "Could not save interval task, ";
        MockHttpServletRequestBuilder createIntervalTask = post(
                "/interval-task-group/" + nonExistentIntervalTaskGroupID)
                .with(csrf())
                .param("noRainOnly", String.valueOf(false))
                .param("intervalTaskName", "Test Interval Task 1");
        mockMvc.perform(createIntervalTask)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }


    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewIntervalTaskGroup() throws Exception {
        int intervalInDays = 3;
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService
                .saveIntervalTaskGroup(any(IntervalTaskGroup.class)))
                .thenReturn(testIntervalTaskGroup);
        MockHttpServletRequestBuilder createTask = post("/interval-task-groups")
                .with(csrf())
                .param("intervalInDays", String.valueOf(intervalInDays))
                .param("taskGroupName", "Test Interval Task Group 1");
        mockMvc.perform(createTask)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-groups"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testSaveNewQuarterlyIntervalTaskGroup() throws Exception  {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        when(intervalTaskGroupService
                .getIntervalTaskGroup(anyLong()))
                .thenReturn(testIntervalTaskGroup);
        when(intervalTaskGroupService
                .saveIntervalTaskGroupAppliedQuarterly(
                        any(IntervalTaskGroupAppliedQuarterly.class)))
                .thenReturn(testITGAQ1);
        MockHttpServletRequestBuilder createQITG =
                post("/submit-quarterly-interval-task-group-scheduled/"
                        + quarter + "/" + thisYear +"/")
                        .with(csrf())
                        .param("recurringTaskSchedulerId",
                                testIntervalTaskGroup.getId().toString());
        mockMvc.perform(createQITG)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/quarterly-interval-task-groups-scheduled"));
    }

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

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowIntervalTaskGroup() throws Exception{
        List<IntervalTask> testIntervalTasks = new ArrayList<>();
        testIntervalTasks.add(testIntervalTask);
        testIntervalTasks.add(testIntervalTask2);
        testIntervalTaskGroup.setIntervalTasks(testIntervalTasks);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(intervalTaskGroupService.getIntervalTaskGroup(
                anyLong()))
                .thenReturn(testIntervalTaskGroup);
        mockMvc
                .perform(get("/interval-task-group/"
                        + testIntervalTaskGroup.getId()))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("intervalTaskGroup"))
                // these substrings are the titles of the
                // tasks and interval task group created above
                // It makes sure that the expected objects
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task Group 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Interval Task 2")))
                .andExpect(view().name("tasks/interval-task-group"));
    }
}
