package MaintenanceManager.MaintenanceManager.controllers.endPointTests.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MonthlyTaskSchedulerRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;


    // test endpoint to delete the MonthlyTaskScheduler, which was created in the fourth
    // test method run -- testSaveNewQuarterlyMonthlyTask
    @Test
    @Order(10)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteMonthlyMaintenanceTask() throws Exception {
        MonthlyTaskScheduler testMonthlyTaskScheduler
                = monthlyTaskSchedulerRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteMonthlyTaskScheduler = post(
                "/delete-monthly-task-scheduler/"
                        + testMonthlyTaskScheduler.getId());
        mockMvc.perform(deleteMonthlyTaskScheduler)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monthly-tasks"));
    }

    // test endpoint to delete the MonthlyTaskScheduler, for a non-existent ID
    // this should show the error page, and there should be no MonthlyTaskScheduler
    // objects in the db because the only one was deleted in the method above
    @Test
    @Order(11)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteMonthlyMaintenanceTaskFailure() throws Exception {
        Long nonExistentMonthlyTaskSchedulerID = 2829L;
        String message = "Cannot delete, monthly task with id: 2829" +
                " does not exist.";
        MockHttpServletRequestBuilder deleteMonthlyTaskScheduler = post(
                "/delete-monthly-task-scheduler/"
                        + nonExistentMonthlyTaskSchedulerID);
        mockMvc.perform(deleteMonthlyTaskScheduler)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    // test endpoint to delete the MonthlyTaskAppliedQuarterly, which was created in the first
    // test method run -- testSaveNewMonthlyTaskScheduler
    @Test
    @Order(8)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteMonthlyTaskAppliedQuarterly() throws Exception {
        MonthlyTaskAppliedQuarterly testMTAQ = monthlyTaskAppliedQuarterlyRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteMTAQ = post(
                "/delete-monthly-task-applied-quarterly/" + testMTAQ.getId());
        mockMvc.perform(deleteMTAQ)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-monthly-tasks-scheduled"));
    }

    // test endpoint to delete the MonthlyTaskAppliedQuarterly, for a non-existent ID
    // this should show the error page, and there should be no MonthlyTaskAppliedQuarterly
    // objects in the db because the only one was deleted in the method above
    @Test
    @Order(9)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteMonthlyTaskAppliedQuarterlyFailure() throws Exception {
        Long nonExistentMTAQID = 2829L;
        String message = "Cannot delete, monthly task " +
                "applied quarterly with id: 2829" +
                " does not exist.";
        MockHttpServletRequestBuilder deleteMTAQ = post(
                "/delete-monthly-task-applied-quarterly/" + nonExistentMTAQID);
        mockMvc.perform(deleteMTAQ)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }


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

    // this will test saving a monthly task scheduler applied quarterly. It will apply the monthly
    // task scheduler created in the testSaveNewMonthlyTaskScheduler and apply it to the first
    // quarter of the current year
    @Test
    @Order(4)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewQuarterlyMonthlyTask() throws Exception  {
        MonthlyTaskScheduler testMonthlyTaskScheduler = monthlyTaskSchedulerRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder createMonthlyTaskScheduler =
                post("/submit-quarterly-monthly-tasks-scheduled/"
                + quarter + "/" + thisYear +"/")
                .param("monthlyTaskScheduler", testMonthlyTaskScheduler.getId().toString());
        mockMvc.perform(createMonthlyTaskScheduler)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quarterly-monthly-tasks-scheduled"));
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

    @Test
    @Order(5)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserQuarterlyMonthlyTasks() throws Exception{
        mockMvc
                .perform(get("/quarterly-monthly-tasks-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qMonthlyTasks"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created in the
                // testSaveNewMonthlyTaskScheduler
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test monthly task scheduler")))
                .andExpect(view().name("tasks/quarterly-monthly-tasks-scheduled"));
    }

    // tests the endpoint of the form for the monthly schedulers to be applied to a specific quarter/year
    // in this case, it is the current year and quarter 2, so the "Test monthly task scheduler" should
    // display as one of the options of a scheduler which can be applied in the template selector
    @Test
    @Order(6)
    @WithUserDetails("Test Maintenance User1")
    public void testShowApplyMonthlySchedulerFormPage() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q2";
        mockMvc
                .perform(request(HttpMethod.GET,
                "/apply-monthly-schedulers")
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("monthlyTasks"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qMonthlyTask"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test monthly task scheduler")))
                .andExpect(view().name("tasks/apply-monthly-schedulers"));
    }

    // tests that no monthly schedulers can be selected for Q1 of the current year
    // because the only monthly schedulers has already been applied to that quarter/year
    @Test
    @Order(7)
    @WithUserDetails("Test Maintenance User1")
    public void testShowApplyMonthlySchedulerFormPageNoneAvailable() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-monthly-schedulers")
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
                // "None Available" is shown in the empty selector in the form template
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("None Available")))
                .andExpect(view().name("tasks/apply-monthly-schedulers"));
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
               .andExpect(view().name("tasks/create-monthly-task-scheduler"));
   }
}
