package MaintenanceManager.MaintenanceManager.controllerEndpoints.tasks;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
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
public class MaintenanceTaskControllerEndpointTest {

    @Autowired
    MaintenanceTaskRepo maintenanceTaskRepo;

    @Autowired
    MockMvc mockMvc;

    // test endpoint to change task status to 'completed'. Only one task should be in the test
    // database, and it is created in the testSaveNewTask method below (executed first)
    @Test
    @Order(7)
    @WithUserDetails("Test Maintenance User1")
    public void testConfirmTaskCompletion() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        MockHttpServletRequestBuilder rescheduleTask = post(
                "/confirm-task-completion/" + testTask.getId());
        mockMvc.perform(rescheduleTask)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    // test endpoint to delete task. Only one task should be in the test
    // database, and it is created in the testSaveNewTask method below (executed first)
    // this should be the 2nd to last test method executed, so that the task still
    // exists for the endpoint test methods below
    @Test
    @Order(10)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteTask() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        MockHttpServletRequestBuilder deleteTask = post(
                "/delete-single-task/" + testTask.getId());
        mockMvc.perform(deleteTask)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    // test endpoint to delete task with a non-existent id, so that it renders an error page
    // with a message. The only task should have been deleted in the method above
    @Test
    @Order(11)
    @WithUserDetails("Test Maintenance User1")
    public void testDeleteTaskFailure() throws Exception {
        Long nonExistentID = 12920L;
        String message = "Cannot delete, task with id: "
                + nonExistentID + " does not exist";
        MockHttpServletRequestBuilder deleteTask = post(
                "/delete-single-task/" + nonExistentID);
        mockMvc.perform(deleteTask)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    // test endpoint to submit rescheduled task. Only one task should be in the test
    // database, and it is created in the testSaveNewTask method below (executed first)
    @Test
    @Order(8)
    @WithUserDetails("Test Maintenance User1")
    public void testRescheduleTask() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        String tomorrow = LocalDate.now().plusDays(1).toString();
        MockHttpServletRequestBuilder rescheduleTask = post(
                "/submit-reschedule-task-form/" + testTask.getId())
                .param("date", tomorrow)
                .param("comments", "Test rescheduling of task");
        mockMvc.perform(rescheduleTask)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    // test endpoint to reschedule maintenance task with the incorrect id, so that
    // the query fails and an error page is rendered
    @Test
    @Order(9)
    @WithUserDetails("Test Maintenance User1")
    public void testRescheduleTaskError() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        Long correctIDPlusOne = testTask.getId() + 1;
        String message = "Cannot update, task does not exist!";
        String tomorrow = LocalDate.now().plusDays(1).toString();
        MockHttpServletRequestBuilder rescheduleTask = post(
                "/submit-reschedule-task-form/" + correctIDPlusOne)
                .param("date", tomorrow)
                .param("comments", "Test rescheduling of task");
        mockMvc.perform(rescheduleTask)
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }


    // this posts a task, which will then be viewed in the task by date and task by month tests
    // the date of the task is today, the repo is cleared (maintenance_test database schema)
    // in the MaintenanceManagerApplicationTest class whenever the test profile is active
    // it should be the only MaintenanceTask that can be queried and is always on the current
    // date and in the current month
    @Test
    @Order(1)
    @WithUserDetails("Test Maintenance User1")
    public void testSaveNewTask() throws Exception {
        String today = LocalDate.now().toString();
        MockHttpServletRequestBuilder createTask = post("/tasks")
                .param("date", today)
                .param("taskName", "Test task");
        mockMvc.perform(createTask)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks-by-month"));
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Maintenance User1")
    public void testShowAllUserTasksByMonth() throws Exception {
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

    // test endpoint to display form to create a new maintenance task
    @Test
    @Order(6)
    @WithUserDetails("Test Maintenance User1")
    public void testShowSubmitTaskFormPage() throws Exception {
        mockMvc
                .perform(get("/create-single-task"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("maintenanceTask"))
                .andExpect(view().name("tasks/create-single-task"));;
    }

    @Test
    @Order(2)
    @WithUserDetails("Test Maintenance User1")
    public void testShowTaskDetailPage() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        String today = LocalDate.now().toString();
        mockMvc
                .perform(get("/task-detail/" + testTask.getId()))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("editTaskForm"))
                // this substring is the title of the task created in the testPostToTaskURL
                // method below, so it makes sure that the expected object for that date is
                // displayed on the page along with the date
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test task")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(today)))
                .andExpect(view().name("tasks/task-detail"));
    }

    // this will query a non-existent object (id of the only MaintenanceTask in the
    // database plus one), so that an error page will be displayed
    @Test
    @Order(5)
    @WithUserDetails("Test Maintenance User1")
    public void testShowTaskDetailErrorPage() throws Exception {
        MaintenanceTask testTask = maintenanceTaskRepo.findAll().get(0);
        Long correctIDPlusOne = testTask.getId() + 1;
        String message = "Task with id "
                + correctIDPlusOne + " does not exist.";
        mockMvc
                .perform(get("/task-detail/" + correctIDPlusOne))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(message)))
                .andExpect(view().name("error/error"));
    }

    @Test
    @Order(3)
    @WithUserDetails("Test Maintenance User1")
    public void testShowUserTasksByDate() throws Exception {
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
}
