package MaintenanceManager.MaintenanceManager.controllers;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllerEndpoints.tasks.MaintenanceTaskController;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceTaskController.class)
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
    @WithMockUser(roles = {"USER", "MAINTENANCE"})
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
}
