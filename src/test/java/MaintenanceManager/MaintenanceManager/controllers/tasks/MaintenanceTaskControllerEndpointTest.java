package MaintenanceManager.MaintenanceManager.controllers.tasks;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MaintenanceTaskControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("Test Maintenance User1")
    public void testTasksByDateURLModelsAndView() throws Exception {
        mockMvc
                .perform(get("/tasks-by-date/2023-04-10"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("dayAfter"))
                .andExpect(model().attributeExists("dayBefore"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("date"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("tasks/tasks-by-date"));
    }

    @Test
    @WithUserDetails("Test Maintenance User1")
    public void testPostToTaskURL() throws Exception {
        MockHttpServletRequestBuilder createTask = post("/tasks")
                //.modelAttribute
                .param("date", "2023-10-23")
                .param("taskName", "Test task");
        System.out.println("*********Submitting the following mock task**********");
        System.out.println(createTask);
        mockMvc.perform(createTask)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    @Test
    @WithUserDetails("Test Maintenance User1")
    public void testTasksByMonthURLModelsAndView() throws Exception {
        mockMvc
                .perform(get("/tasks-by-month"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("tasks/tasks-by-month"));
    } // tasks-by-date/2023-04-10
}
