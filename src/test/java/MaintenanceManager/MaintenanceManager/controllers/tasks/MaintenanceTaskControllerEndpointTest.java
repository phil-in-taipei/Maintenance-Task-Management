package MaintenanceManager.MaintenanceManager.controllers.tasks;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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
import org.springframework.test.web.servlet.result.ModelResultMatchers;

import java.time.LocalDate;

import static java.time.LocalTime.now;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class MaintenanceTaskControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(2)
    @WithUserDetails("Test Maintenance User1")
    public void testTasksByDateURLModelsAndView() throws Exception {
        String today = LocalDate.now().toString();
        mockMvc
                .perform(get("/tasks-by-date/" + today))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("dayAfter"))
                .andExpect(model().attributeExists("dayBefore"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("date"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created in the testPostToTaskURL
                // method below, so it makes sure that the expected object for that date is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(containsString("Test task")))
                .andExpect(view().name("tasks/tasks-by-date"));
    }

    // this posts a task, which will then be viewed in the task by date test
    @Test
    @Order(1)
    @WithUserDetails("Test Maintenance User1")
    public void testPostToTaskURL() throws Exception {
        String today = LocalDate.now().toString();
        MockHttpServletRequestBuilder createTask = post("/tasks")
                .param("date", today)
                .param("taskName", "Test task");
        mockMvc.perform(createTask)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Maintenance User1")
    public void testTasksByMonthURLModelsAndView() throws Exception {
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
                // this substring is the title of the task created in the testPostToTaskURL
                // method below, so it makes sure that the expected object for that date is
                // displayed on the page -- because the task is scheduled for the current day,
                // and this page shows tasks for the current month
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test task")))
                .andExpect(view().name("tasks/tasks-by-month"));
    }
}
