package MaintenanceManager.MaintenanceManager.controllers.unitTests.tasks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.tasks.MaintenanceTaskController;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.models.user.*;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceTaskController.class)
//@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
public class MaintenanceTaskControllerUnitTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    UserPrincipalRepo userPrincipalRepo;

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

    MaintenanceTask testTask = MaintenanceTask.builder()
            .id(1L)
            .taskName("Test Task 1")
            .status(TaskStatusEnum.COMPLETED)
            .date(LocalDate.now())
            .user(testUser)
            .build();

    MaintenanceTask testTask2 = MaintenanceTask.builder()
            .id(2L)
            .taskName("Test Task 2")
            .status(TaskStatusEnum.COMPLETED)
            .date(LocalDate.now())
            .user(testUser)
            .build();

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testConfirmTaskCompletion() throws Exception {

        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(testTask);
        mockMvc.
                perform(request(HttpMethod.GET, "/confirm-task-completion/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteTask() throws Exception {
        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(testTask);
       mockMvc.
               perform(request(HttpMethod.GET, "/delete-single-task/1"))
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testDeleteTaskFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, task with id: "
                + nonExistentID + " does not exist";
        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(null);

        mockMvc
                .perform(request(HttpMethod.GET, "/delete-single-task/12920"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserTasksByMonth() throws Exception {
        LocalDate today = LocalDate.now();
        int thisMonthInt = today.getMonthValue();
        int thisYearInt = today.getYear();
        LocalDate monthBegin = today.withDayOfMonth(1);
        LocalDate monthEnd = today.plusMonths(1).
                withDayOfMonth(1).minusDays(1);
        testTask.setDate(LocalDate.of(thisYearInt, 1, thisMonthInt));
        testTask2.setDate(LocalDate.of(thisYearInt, 5, thisMonthInt));
        List<MaintenanceTask> tasksInCurrentMonth = new ArrayList<>();
        tasksInCurrentMonth.add(testTask);
        tasksInCurrentMonth.add(testTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(maintenanceTaskService.getAllUserTasksInDateRange(anyString(),
                eq(monthBegin), eq(monthEnd)))
                .thenReturn(tasksInCurrentMonth);
        mockMvc
                .perform(get("/tasks-by-month"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("user"))
                // this is the substring of the current year
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(String.valueOf(thisYearInt))))
                //this is the substring of the current month
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(String.valueOf(thisMonthInt))))
                // these substrings are the titles of the tasks created during the current month
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 2")))
                // this is the substring of the username
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name("tasks/tasks-by-month"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowTaskDetailPage() throws Exception {
        String today = LocalDate.now().toString();
        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(testTask);

        mockMvc
                .perform(get("/task-detail/" + 1))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("editTaskForm"))
                // This substring is the title of the task created above
                // It makes sure that the expected object for that date is
                // displayed on the page along with the date
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(today)))
                .andExpect(view().name("tasks/task-detail"));
    }

    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowTaskDetailErrorPage() throws Exception {
        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(null);
        Long testNonExistentID = 2L;
        String message = "Task with id "
                + testNonExistentID + " does not exist.";
        mockMvc
                .perform(get("/task-detail/" + testNonExistentID))
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
    public void testShowUserTasksByDate() throws Exception {
        String today = LocalDate.now().toString();
        List<MaintenanceTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(testTask);
        tasksOnCurrentDate.add(testTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(maintenanceTaskService.getAllUserTasksByDate(anyString(), eq(LocalDate.now())))
                .thenReturn(tasksOnCurrentDate);
        mockMvc.perform(get("/tasks-by-date/" + today))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("dayAfter"))
                .andExpect(model().attributeExists("dayBefore"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("date"))
                .andExpect(model().attributeExists("user"))
                // this substrings are the titles of the task created above
                // method below, so it makes sure that the expected object for that date is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 2")))
                .andExpect(view().name("tasks/tasks-by-date"));
    }
}
