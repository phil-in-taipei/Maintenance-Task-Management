package MaintenanceManager.MaintenanceManager.controllers.unitTests.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.controllers.tasks.MaintenanceTaskController;
import MaintenanceManager.MaintenanceManager.controllers.tasks.MonthlyTaskSchedulerController;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.models.user.*;

import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.tasks.MonthlyTaskSchedulingService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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



    @Test
    @WithMockUser(roles = {"USER", "MAINTENANCE"}, username = "testuser")
    public void testShowAllUserMonthlyTasks() throws Exception {
        MonthlyTaskScheduler testMonthlyTaskScheduler = MonthlyTaskScheduler.builder()
                .id(1L)
                .monthlyTaskName("Test Monthly Task Scheduler 1")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        System.out.println("This is the first monthly task scheduler:");
        System.out.println(testMonthlyTaskScheduler);
        MonthlyTaskScheduler testMonthlyTaskScheduler2 = MonthlyTaskScheduler.builder()
                .id(2L)
                .monthlyTaskName("Test Monthly Task Scheduler 2")
                .dayOfMonth(1)
                .user(testUser)
                .build();
        System.out.println("This is the second monthly task scheduler:");
        System.out.println(testMonthlyTaskScheduler2);
        List<MonthlyTaskScheduler> usersMonthlyTaskSchedulers = new ArrayList<>();
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler);
        usersMonthlyTaskSchedulers.add(testMonthlyTaskScheduler2);
        System.out.println("This is the test user:");
        System.out.println(testUser);
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        when(monthlyTaskSchedulingService
                .getAllUsersMonthlyTaskSchedulers(anyString()))
                .thenReturn(usersMonthlyTaskSchedulers);
        mockMvc
                .perform(get("/monthly-tasks"))
                .andDo(print())
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
}
