package MaintenanceManager.MaintenanceManager.controllers.endPointTests.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskScheduler;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.WeeklyTaskSchedulerRepo;
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

import java.time.DayOfWeek;
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
public class WeeklyTaskSchedulerControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @Autowired
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    // test endpoint to delete the WeeklyTaskScheduler, which was created in the fourth
    // test method run -- testSaveNewQuarterlyWeeklyTask
    @Test
    @Order(10)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteWeeklyMaintenanceTask() throws Exception {
        WeeklyTaskScheduler testWeeklyTaskScheduler
                = weeklyTaskSchedulerRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteWeeklyTaskScheduler = post(
                "/delete-weekly-task-scheduler/"
                        + testWeeklyTaskScheduler.getId());
        mockMvc.perform(deleteWeeklyTaskScheduler)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/weekly-tasks"));
    }

    // test endpoint to delete the WeeklyTaskScheduler, for a non-existent ID
    // this should show the error page, and there should be no WeeklyTaskScheduler
    // objects in the db because the only one was deleted in the method above
    @Test
    @Order(11)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteWeeklyMaintenanceTaskFailure() throws Exception {
        Long nonExistentWeeklyTaskSchedulerID = 2829L;
        String message = "Cannot delete, weekly task with id: 2829" +
                " does not exist.";
        MockHttpServletRequestBuilder deleteMonthlyTaskScheduler = post(
                "/delete-weekly-task-scheduler/"
                        + nonExistentWeeklyTaskSchedulerID);
        mockMvc.perform(deleteMonthlyTaskScheduler)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    // test endpoint to delete the WeeklyTaskAppliedQuarterly, which was created in the first
    // test method run -- testSaveNewWeeklyTaskScheduler
    @Test
    @Order(8)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteWeeklyTaskAppliedQuarterly() throws Exception {
        WeeklyTaskAppliedQuarterly testWTAQ = weeklyTaskAppliedQuarterlyRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteMTAQ = post(
                "/delete-weekly-task-applied-quarterly/" + testWTAQ.getId());
        mockMvc.perform(deleteMTAQ)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-weekly-tasks-scheduled"));
    }

    // test endpoint to delete the WeeklyTaskAppliedQuarterly, for a non-existent ID
    // this should show the error page, and there should be no WeeklyTaskAppliedQuarterly
    // objects in the db because the only one was deleted in the method above
    @Test
    @Order(9)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteWeeklyTaskAppliedQuarterlyFailure() throws Exception {
        Long nonExistentWTAQID = 2829L;
        String message = "Cannot delete, weekly task " +
                "applied quarterly with id: 2829" +
                " does not exist.";
        MockHttpServletRequestBuilder deleteMTAQ = post(
                "/delete-weekly-task-applied-quarterly/" + nonExistentWTAQID);
        mockMvc.perform(deleteMTAQ)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewQuarterlyWeeklyTask() throws Exception  {
        WeeklyTaskScheduler testWeeklyTaskScheduler = weeklyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder createWeeklyTaskScheduler =
                post("/submit-quarterly-weekly-tasks-scheduled/"
                        + quarter + "/" + thisYear +"/")
                        .param("recurringTaskSchedulerId",
                                testWeeklyTaskScheduler.getId().toString());
        mockMvc.perform(createWeeklyTaskScheduler)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-weekly-tasks-scheduled"));
    }

    // this posts a weekly task scheduler, which will then be used in the other endpoint test
    // methods in this class and then deleted from the database on the 2nd to last test
    @Test
    @Order(1)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewWeeklyTaskScheduler() throws Exception {
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        MockHttpServletRequestBuilder createTask = post("/weekly-tasks")
                .param("dayOfWeek", String.valueOf(dayOfWeek))
                .param("weeklyTaskName", "Test weekly task scheduler");
        mockMvc.perform(createTask)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/weekly-tasks"));
    }

    @Test
    @Order(5)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserQuarterlyWeeklyTasks() throws Exception{
        mockMvc
                .perform(get("/quarterly-weekly-tasks-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qWeeklyTasks"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created in the
                // testSaveNewWeeklyTaskScheduler
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test weekly task scheduler")))
                .andExpect(view().name("tasks/quarterly-weekly-tasks-scheduled"));
    }

    @Test
    @Order(2)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserWeeklyTasks() throws Exception {
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
                // this substring is the title of the task created in the testSaveNewWeeklyTaskScheduler
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test weekly task scheduler")))
                .andExpect(view().name("tasks/weekly-task-schedulers"));
    }

    // tests the endpoint of the form for the weekly schedulers to be applied to a specific quarter/year
    // in this case, it is the current year and quarter 2, so the "Test weekly task scheduler" should
    // display as one of the options of a scheduler which can be applied in the template selector
    @Test
    @Order(6)
    @WithUserDetails("Test Maintenance User1")
    public void testShowApplyWeeklySchedulerFormPage() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q2";
        MockHttpServletRequestBuilder applyWeeklyTaskScheduler =
                post("/apply-weekly-schedulers")
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);

        mockMvc
                .perform(applyWeeklyTaskScheduler)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("weeklyTasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qWeeklyTask"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test weekly task scheduler")))
                .andExpect(view().name("tasks/apply-weekly-schedulers"));
    }

    // tests that no weekly schedulers can be selected for Q1 of the current year
    // because the only weekly schedulers has already been applied to that quarter/year
    @Test
    @Order(7)
    @WithUserDetails("Test Maintenance User1")
    public void testShowApplyWeeklySchedulerFormPageNoneAvailable() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-weekly-schedulers")
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);

        mockMvc
                .perform(applyMonthlyTaskScheduler)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("weeklyTasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qWeeklyTask"))
                // "None Available" is shown in the empty selector in the form template
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("None Available")))
                .andExpect(view().name("tasks/apply-weekly-schedulers"));
    }

    // test endpoint to display form to create a new weekly task scheduler
    @Test
    @Order(3)
    @WithUserDetails("Test Maintenance User1")
    public void testShowCreateWeeklyTaskFormPage() throws Exception {
        mockMvc
                .perform(get("/create-weekly-task-scheduler"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("weeklyTaskScheduler"))
                .andExpect(view().name("tasks/create-weekly-task-scheduler"));
    }
}
