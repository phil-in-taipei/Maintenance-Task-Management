package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskGroupRepo;
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
public class IntervalTaskGroupControllerEndpointTest {

    @Autowired
    IntervalTaskAppliedQuarterlyRepo intervalTaskAppliedQuarterlyRepo;

    @Autowired
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    MockMvc mockMvc;

    // endpoint test to save an interval task, which will be a member of the interval task
    // group created in the testSaveNewIntervalTaskGroup. It will first query the repo
    // to get the id of the interval task group, which will be used as the path variable
    // in the post request
    @Test
    @Order(2)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewIntervalTask() throws Exception {
        IntervalTaskGroup testIntervalTaskGroup = intervalTaskGroupRepo.findAll().get(0);
        MockHttpServletRequestBuilder createIntervalTask = post(
                "/interval-task-group/" + testIntervalTaskGroup.getId())
                .param("noRainOnly", String.valueOf(false))
                .param("intervalTaskName", "Test interval task");
        mockMvc.perform(createIntervalTask)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-groups"));
    }

    // endpoint test to save an interval task, which will be a member of a non-existent
    // interval task group. Because the path variable is the id of a group that does
    // not exist, saving should fail, and it should return an error page
    @Test
    @Order(3)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewIntervalTaskFailure() throws Exception {
        Long nonExistentIntervalTaskGroupID = 2829L;
        String message = "Could not save interval task, ";
        MockHttpServletRequestBuilder createIntervalTask = post(
                "/interval-task-group/" + nonExistentIntervalTaskGroupID)
                .param("noRainOnly", String.valueOf(false))
                .param("intervalTaskName", "Test interval task");
        mockMvc.perform(createIntervalTask)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));

    }

    // this posts an interval task group, which will then be used in the other endpoint test
    // methods in this class and then deleted from the database on the 2nd to last test
    @Test
    @Order(1)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewIntervalTaskGroup() throws Exception {
        int intervalInDays = 10;
        MockHttpServletRequestBuilder createIntervalTaskGroup =
                post("/interval-task-groups")
                .param("intervalInDays", String.valueOf(intervalInDays))
                .param("taskGroupName", "Test interval task group");
        mockMvc.perform(createIntervalTaskGroup)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/interval-task-groups"));
    }

    // this will test saving an interval task group applied quarterly. It will apply the interval
    // task group created in the testSaveNewIntervalTaskGroup and apply it to the first
    // quarter of the current year
    @Test
    @Order(4)
    @WithUserDetails("Test Maintenance User1")
    public void saveNewQuarterlyIntervalTaskGroup() throws Exception  {
        IntervalTaskGroup testIntervalTaskGroup = intervalTaskGroupRepo.findAll().get(0);
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder createQITG =
                post("/submit-quarterly-interval-task-group-scheduled/"
                        + quarter + "/" + thisYear +"/")
                        .param("intervalTaskGroup",
                                testIntervalTaskGroup.getId().toString());
        mockMvc.perform(createQITG)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/quarterly-interval-task-groups-scheduled"));
    }

    @Test
    @Order(5)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserIntervalTaskGroups() throws Exception {
        mockMvc
                .perform(get("/interval-task-groups"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("intervalTaskGroups"))
                .andExpect(model().attributeExists("intervalTaskQuarterAndYear"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created in the testSaveNewIntervalTaskGroup
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test interval task group")))
                .andExpect(view().name("tasks/interval-task-groups"));
    }

    @Test
    @Order(6)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserQuarterlyIntervalTaskGroups() throws Exception{
        mockMvc
                .perform(get("/quarterly-interval-task-groups-scheduled"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                // these are the expected model attributes
                .andExpect(model().attributeExists("qITG"))
                .andExpect(model().attributeExists("user"))
                // this substring is the title of the task created in the
                // testSaveNewIntervalTaskGroup
                // method below, so it makes sure that the expected object is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test interval task group")))
                .andExpect(view().name(
                        "tasks/quarterly-interval-task-groups-scheduled"));
    }

    // tests the endpoint of the form for the interval task group to be applied to a specific quarter/year
    // in this case, it is the current year and quarter 2, so the "Test interval task group" should
    // display as one of the options of an ITG which can be applied in the template selector
    @Test
    @Order(8)
    @WithUserDetails("Test Maintenance User1")
    public void showApplyITGSchedulerFormPage() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q2";
        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-interval-task-group-schedulers")
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);

        mockMvc
                .perform(applyMonthlyTaskScheduler)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("intervalTaskGroups"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qITG"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test interval task group")))
                .andExpect(view().name(
                        "tasks/apply-interval-task-group-schedulers"));
    }

    // tests that no interval task groups can be selected for Q1 of the current year
    // because the only interval task group has already been applied to that quarter/year
    @Test
    @Order(8)
    @WithUserDetails("Test Maintenance User1")
    public void showApplyITGSchedulerFormPageNonAvailable() throws Exception {
        int thisYear = LocalDate.now().getYear();
        String quarter = "Q1";
        MockHttpServletRequestBuilder applyMonthlyTaskScheduler =
                post("/apply-interval-task-group-schedulers")
                        .param("year", String.valueOf(thisYear))
                        .param("quarter", quarter);

        mockMvc
                .perform(applyMonthlyTaskScheduler)
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("intervalTaskGroups"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("quarter"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("qITG"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("None Available")))
                .andExpect(view().name(
                        "tasks/apply-interval-task-group-schedulers"));
    }


    // test endpoint to display form to create a new interval task group
    @Test
    @Order(7)
    @WithUserDetails("Test Maintenance User1")
    public void testShowCreateIntervalTaskGroupFormPage() throws Exception {
        mockMvc
                .perform(get("/create-interval-task-group"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("intervalTaskGroup"))
                .andExpect(view().name("tasks/create-interval-task-group"));
    }
}
