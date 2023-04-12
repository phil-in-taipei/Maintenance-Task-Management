package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class MonthlyTaskSchedulerControllerEndpointTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;


    // this posts a monthly task scheduler, which will then be used in the other endpoint test
    // methods in this class and then deleted from the database on the 2nd to last test
    @Test
    @Order(1)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewMonthlyTaskScheduler() throws Exception {
        int dayOfMonth = 1;
        MockHttpServletRequestBuilder createTask = post("/monthly-tasks")
                .param("dayOfMonth", String.valueOf(dayOfMonth))
                .param("monthlyTaskName", "Test monthly task scheduler");
        mockMvc.perform(createTask)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monthly-tasks"));
    }

   @Test
   @Order(2)
   @WithUserDetails("Test Maintenance User1")
   public void testShowAllUserMonthlyTasks() throws Exception {
       mockMvc
               .perform(get("/monthly-tasks"))
               .andExpect(MockMvcResultMatchers.content()
                       .contentType("text/html;charset=UTF-8"))
               .andExpect(status().is2xxSuccessful())
               // these are the expected model attributes
               .andExpect(model().attributeExists("monthlyTasks"))
               .andExpect(model().attributeExists("monthlyTaskQuarterAndYear"))
               .andExpect(model().attributeExists("user"))
               // this substring is the title of the task created in the testSaveNewMonthlyTaskScheduler
               // method below, so it makes sure that the expected object is
               // displayed on the page
               .andExpect(MockMvcResultMatchers.content().string(
                       containsString("Test monthly task scheduler")))
               .andExpect(view().name("tasks/monthly-task-schedulers"));
   }

   // test endpoint to display form to create a new monthly task scheduler
   @Test
   @Order(3)
   @WithUserDetails("Test Maintenance User1")
   public void testShowCreateMonthlyTaskFormPage() throws Exception {
       mockMvc
               .perform(get("/create-monthly-task-scheduler"))
               //.andDo(print())
               .andExpect(MockMvcResultMatchers.content()
                       .contentType("text/html;charset=UTF-8"))
               .andExpect(status().is2xxSuccessful())
               .andExpect(model().attributeExists("monthlyTaskScheduler"))
               .andExpect(view().name("tasks/create-monthly-task-scheduler"));;
   }
}
