package MaintenanceManager.MaintenanceManager.controllers;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllerEndpoints.tasks.MaintenanceTaskController;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceTaskController.class)
//@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
public class MaintenanceTaskControllerUnitTest {

    @Autowired
    MockMvc mockMvc;


    //@Autowired
    //RestTemplate restTemplate;

    @MockBean
    AuthorityRepo authorityRepo;

    @MockBean
    UserPrincipalRepo userPrincipalRepo;

    @MockBean
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

    @Test
    //@WithUserDetails("Test Maintenance User1")
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowTaskDetailPage() throws Exception {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        String today = LocalDate.now().toString();
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .status(TaskStatusEnum.COMPLETED)
                .date(LocalDate.now())
                .user(testUser)
                .build();
        when(maintenanceTaskService.getMaintenanceTask(anyLong()))
                .thenReturn(maintenanceTask);

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
    @WithMockUser(roles = {"USER", "MAINTENANCE"})
   // @WithUserDetails("Test Maintenance User1")
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
    //@ParameterizedTest
   // @WithUserDetails("Test Maintenance User1")
   //@WithSecurityContext;
    //@WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    //@AutoConfigureMockMvc(secure = false)
    public void testShowUserTasksByDate() throws Exception {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
        String today = LocalDate.now().toString();
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .status(TaskStatusEnum.COMPLETED)
                .date(LocalDate.now())
                .user(testUser)
                .build();
        MaintenanceTask maintenanceTask2 = MaintenanceTask.builder()
                .id(2L)
                .taskName("Test Task 2")
                .status(TaskStatusEnum.COMPLETED)
                .date(LocalDate.now())
                .user(testUser)
                .build();
        List<MaintenanceTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(maintenanceTask);
        tasksOnCurrentDate.add(maintenanceTask2);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(maintenanceTaskService.getAllUserTasksByDate(anyLong(), eq(LocalDate.now())))
                .thenReturn(tasksOnCurrentDate);
        //mockMvc.perform(get("/tasks-by-date/" + today))/
         mockMvc.perform(MockMvcRequestBuilders.request(
                HttpMethod.GET, "/tasks-by-date/" + today)

                // this will not work because user details here cannot be cast to the
                 // custom UserPrincipal
                .with(user("testuser").password("testpassword")))
                //.andDo(print())
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
                .andExpect(MockMvcResultMatchers.content().string(containsString("Test Task 1")))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Test Task 2")))
                .andExpect(view().name("tasks/tasks-by-date"));
    }
}
