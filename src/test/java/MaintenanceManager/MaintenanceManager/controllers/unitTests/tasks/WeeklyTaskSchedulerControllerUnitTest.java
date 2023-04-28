package MaintenanceManager.MaintenanceManager.controllers.unitTests.tasks;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.tasks.WeeklyTaskSchedulerController;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.tasks.WeeklyTaskSchedulingService;
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

import java.time.DayOfWeek;
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

@WebMvcTest(WeeklyTaskSchedulerController.class)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WeeklyTaskSchedulerControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    MaintenanceTaskService maintenanceTaskService;

    @MockBean
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

    @MockBean
    UserDetailsServiceImplementation userService;


    UserMeta userMeta = UserMeta.builder()
            .id(1L)
            .email("testuser@gmx.com")
            .surname("Test")
            .givenName("User")
            .age(50)
            .build();
    Authority authority1 = Authority.builder().authority(AuthorityEnum.ROLE_USER).build();
    Authority authority2 = Authority.builder().authority(AuthorityEnum.ROLE_MAINTENANCE).build();
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

    WeeklyTaskScheduler testWeeklyTaskScheduler1 = WeeklyTaskScheduler.builder()
            .id(1L)
            .weeklyTaskName("Test Weekly Task Scheduler 1")
            .dayOfWeek(DayOfWeek.MONDAY)
            .user(testUser)
            .build();
    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly1
            = WeeklyTaskAppliedQuarterly
            .builder()
            .id(1L)
            .weeklyTaskScheduler(testWeeklyTaskScheduler1)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();
    WeeklyTaskScheduler testWeeklyTaskScheduler2 = WeeklyTaskScheduler.builder()
            .id(1L)
            .weeklyTaskName("Test Weekly Task Scheduler 2")
            .dayOfWeek(DayOfWeek.TUESDAY)
            .user(testUser)
            .build();
    WeeklyTaskAppliedQuarterly testWeeklyTaskAppliedQuarterly2
            = WeeklyTaskAppliedQuarterly
            .builder()
            .id(2L)
            .weeklyTaskScheduler(testWeeklyTaskScheduler2)
            .year(2023)
            .quarter(QuarterlySchedulingEnum.Q2)
            .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteWeeklyMaintenanceTask() throws Exception {
        when(weeklyTaskSchedulingService
                .getWeeklyTaskScheduler(anyLong()))
                .thenReturn(testWeeklyTaskScheduler1);
        mockMvc.
                perform(request(HttpMethod.GET, "/delete-weekly-task-scheduler/"
                        + testWeeklyTaskScheduler1.getId()))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/weekly-tasks"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteWeeklyMaintenanceTaskFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, weekly task with id: "
                + nonExistentID + " does not exist";
        when(weeklyTaskSchedulingService
                .getWeeklyTaskScheduler(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-weekly-task-scheduler/"
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
    public void testDeleteWeeklyTaskAppliedQuarterly() throws Exception {
        when(weeklyTaskSchedulingService
                .getWeeklyTaskAppliedQuarterly(anyLong()))
                .thenReturn(testWeeklyTaskAppliedQuarterly1);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-weekly-task-applied-quarterly/"
                                + testWeeklyTaskAppliedQuarterly1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-weekly-tasks-scheduled"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteWeeklyTaskAppliedQuarterlyFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, weekly task applied quarterly with id: "
                + nonExistentID + " does not exist";
        when(weeklyTaskSchedulingService
                .getWeeklyTaskAppliedQuarterly(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-weekly-task-applied-quarterly/"
                                + nonExistentID))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserQuarterlyWeeklyTasks() throws Exception {
        List<WeeklyTaskAppliedQuarterly> usersWeeklyTasksAppliedQuarterly = new ArrayList<>();
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly1);
        usersWeeklyTasksAppliedQuarterly.add(testWeeklyTaskAppliedQuarterly2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService.getAllUsersWeeklyTasksAppliedQuarterly(
                anyString()))
                .thenReturn(usersWeeklyTasksAppliedQuarterly);
        mockMvc
                .perform(get("/quarterly-weekly-tasks-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qWeeklyTasks"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created above
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Weekly Task Scheduler 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Weekly Task Scheduler 2")))
                .andExpect(view().name(
                        "tasks/quarterly-weekly-tasks-scheduled"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserWeeklyTasks() throws Exception {
        List<WeeklyTaskScheduler> usersWeeklyTaskSchedulers = new ArrayList<>();
        usersWeeklyTaskSchedulers.add(testWeeklyTaskScheduler1);
        usersWeeklyTaskSchedulers.add(testWeeklyTaskScheduler2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(weeklyTaskSchedulingService.getAllUsersWeeklyTaskSchedulers(anyString()))
                .thenReturn(usersWeeklyTaskSchedulers);
        mockMvc
                .perform(get("/weekly-tasks"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("weeklyTasks"))
                .andExpect(model().attributeExists("weeklyTaskQuarterAndYear"))
                .andExpect(model().attributeExists("user"))
                // these substrings are the titles of the task created above,
                // so it makes sure that the expected objects are
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Weekly Task Scheduler 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Weekly Task Scheduler 2")))
                .andExpect(view().name("tasks/weekly-task-schedulers"));
    }
}
