package MaintenanceManager.MaintenanceManager.controllers.unitTests.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.tasks.MonthlyTaskSchedulerController;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.user.*;

import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.tasks.MonthlyTaskSchedulingService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonthlyTaskSchedulerController.class)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
public class MonthlyTaskSchedulerControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    MaintenanceTaskService maintenanceTaskService;

    @MockBean
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @MockBean
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

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

    MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
            .id(1L)
            .monthlyTaskName("Test Monthly Task Scheduler 1")
            .dayOfMonth(1)
            .user(testUser)
            .build();
    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .id(1L)
                    .monthlyTaskScheduler(testMonthlyTaskScheduler)
                    .year(2023)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();
    MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
            .id(1L)
            .monthlyTaskName("Test Monthly Task Scheduler 2")
            .dayOfMonth(1)
            .user(testUser)
            .build();
    MonthlyTaskAppliedQuarterly testMonthlyTaskAppliedQuarterly2 =
            MonthlyTaskAppliedQuarterly
                    .builder()
                    .id(2L)
                    .monthlyTaskScheduler(testMonthlyTaskScheduler2)
                    .year(2023)
                    .quarter(QuarterlySchedulingEnum.Q2)
                    .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteMonthlyMaintenanceTask() throws Exception {
        when(monthlyTaskSchedulingService
                .getMonthlyTaskScheduler(anyLong()))
                .thenReturn(testMonthlyTaskScheduler);
        mockMvc.
                perform(request(HttpMethod.GET, "/delete-monthly-task-scheduler/"
                        + testMonthlyTaskScheduler.getId()))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monthly-tasks"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteMonthlyMaintenanceTaskFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, monthly task with id: "
               + nonExistentID + " does not exist";
        when(monthlyTaskSchedulingService
                .getMonthlyTaskScheduler(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-monthly-task-scheduler/"
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
    public void testDeleteMonthlyTaskAppliedQuarterly() throws Exception {
        when(monthlyTaskSchedulingService
                .getMonthlyTaskAppliedQuarterly(anyLong()))
                .thenReturn(testMonthlyTaskAppliedQuarterly);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-monthly-task-applied-quarterly/"
                        + testMonthlyTaskAppliedQuarterly.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-monthly-tasks-scheduled"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteMonthlyTaskAppliedQuarterlyFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, monthly task applied quarterly with id: "
                + nonExistentID + " does not exist";
        when(monthlyTaskSchedulingService
                .getMonthlyTaskAppliedQuarterly(anyLong()))
                .thenReturn(null);
        mockMvc.
                perform(request(HttpMethod.GET,
                        "/delete-monthly-task-applied-quarterly/"
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
    public void testShowAllUserMonthlyTasks() throws Exception {
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers(anyString()))
                .thenReturn(usersMonthlyTaskSchedulers);
        mockMvc
                .perform(get("/monthly-tasks"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("monthlyTasks"))
                .andExpect(model().attributeExists("monthlyTaskQuarterAndYear"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created above
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Monthly Task Scheduler 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Monthly Task Scheduler 2")))
                .andExpect(view().name("tasks/monthly-task-schedulers"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserQuarterlyMonthlyTasks() throws Exception{
        List<MonthlyTaskAppliedQuarterly> usersMonthlyTasksAppliedQuarterly = new ArrayList<>();
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly);
        usersMonthlyTasksAppliedQuarterly.add(testMonthlyTaskAppliedQuarterly2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTasksAppliedQuarterly(anyString()))
                .thenReturn(usersMonthlyTasksAppliedQuarterly);
        mockMvc
                .perform(get("/quarterly-monthly-tasks-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qMonthlyTasks"))
                .andExpect(model().attributeExists("user"))
                // these substrings are the titles of the tasks created above
                // , so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Monthly Task Scheduler 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Monthly Task Scheduler 2")))
                .andExpect(view().name("tasks/quarterly-monthly-tasks-scheduled"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowApplyMonthlySchedulerFormPage() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
                        anyString(),
                        eq(QuarterlySchedulingEnum.valueOf(quarter)),
                        eq(thisYear)
                )).thenReturn(usersMonthlyTaskSchedulers);

        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-monthly-schedulers")
                        .with(csrf())
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);
        mockMvc
                .perform(applyMonthlyTaskScheduler)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("monthlyTasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qMonthlyTask"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Monthly Task Scheduler 1"))) //"None Available"
                .andExpect(view().name("tasks/apply-monthly-schedulers"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowApplyMonthlySchedulerFormPageNonAvailable() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q3";
        // this will be an empty array
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
                        anyString(),
                        eq(QuarterlySchedulingEnum.valueOf(quarter)),
                        eq(thisYear)
                )).thenReturn(usersMonthlyTaskSchedulers); // returns empty array (not null!)
        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-monthly-schedulers")
                        .with(csrf())
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);
        mockMvc
                .perform(applyMonthlyTaskScheduler)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("monthlyTasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qMonthlyTask"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("None Available")))
                .andExpect(view().name("tasks/apply-monthly-schedulers"));
    }
}
