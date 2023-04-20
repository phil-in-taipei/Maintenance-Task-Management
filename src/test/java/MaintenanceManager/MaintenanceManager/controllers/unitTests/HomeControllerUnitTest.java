package MaintenanceManager.MaintenanceManager.controllers.unitTests;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.HomeController;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@ContextConfiguration(classes = {MaintenanceManagerApplication.class})
@ActiveProfiles("test")
public class HomeControllerUnitTest {

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
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testLandingPageMaintenanceUser() throws Exception {
        UserPrincipal testUser = userService.loadUserByUsername("testuser");
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
        MaintenanceTask maintenanceTask3 = MaintenanceTask.builder()
                .id(2L)
                .taskName("Test Task 3")
                .status(TaskStatusEnum.PENDING)
                .date(LocalDate.now().minusDays(1))
                .user(testUser)
                .build();
        MaintenanceTask maintenanceTask4 = MaintenanceTask.builder()
                .id(2L)
                .taskName("Test Task 4")
                .status(TaskStatusEnum.PENDING)
                .date(LocalDate.now().minusDays(2))
                .user(testUser)
                .build();
        List<MaintenanceTask> tasksOnCurrentDate = new ArrayList<>();
        tasksOnCurrentDate.add(maintenanceTask);
        tasksOnCurrentDate.add(maintenanceTask2);
        List<MaintenanceTask> tasksOnPreviousDates = new ArrayList<>();
        tasksOnPreviousDates.add(maintenanceTask3);
        tasksOnPreviousDates.add(maintenanceTask4);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(maintenanceTaskService.getAllUserTasksByDate(anyString(), eq(LocalDate.now())))
                .thenReturn(tasksOnCurrentDate);
        when(maintenanceTaskService.getAllUncompletedPastUserTasks(anyString()))
                .thenReturn(tasksOnPreviousDates);
        mockMvc
                .perform(get("/landing"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("uncompletedTasks"))
                // these substrings are for tasks scheduled prior to today
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 3")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 4")))
                .andExpect(model().attributeExists("dailyTasks"))
                // these substrings are for the tasks created on the current day
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 1")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Task 2")))
                // this substring is the name of the user created above
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("testuser")))
                .andExpect(view().name("landing-no-weather"));
    }
}
