package MaintenanceManager.MaintenanceManager.controllers.auth;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class RegistrationControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    // test endpoint to display registration form
    @Test
    @Order(1)
    public void showRegisterForm() throws Exception {
        mockMvc
                .perform(get("/register"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("userRegistration"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("username")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("password")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("confirm password")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("surname")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("email")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("given name")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("sign up")))
                .andExpect(view().name("auth/register"));
    }

    // this will attempt to register a new user and fail because the password
    // and password confirmation do not match
    @Test
    @Order(2)
    public void testSubmitRegisterFormFailure() throws Exception {
        MockHttpServletRequestBuilder testLogin = post(
                "/register")
                .param("surname", "Test")
                .param("givenName", "User2")
                .param("email", "test@gmx.com")
                .param("age", "37")
                .param("username", "Test Maintenance User2")
                .param("password", "correct")
                .param("passwordConfirmation", "incorrect");
        mockMvc.perform(testLogin)
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("errorMsg"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("The passwords do not match!")))
                .andExpect(view().name("auth/register-failure"));
    }

    // note: this will be commented out until the database models are modified
    // to allow for safe deletion of users with proper cascading (underway in new
    // Expenses Tracker project, and will later be applied in this project)
    // Otherwise repeatedly running this method will create an excess of users in
    // the test db. Also, each time this is run, the username must be changed
    // unless there is a deletion endpoint and corresponding test that cleans up after
    // creating the user in this test

    /*
    @Test
    @Order(3)
    public void testSubmitRegisterFormSuccess() throws Exception {
        MockHttpServletRequestBuilder testLogin = post(
                "/register")
                .param("surname", "Test")
                .param("givenName", "User2")
                .param("email", "test@gmx.com")
                .param("age", "37")
                .param("username", "Test Maintenance User2")
                .param("password", "testpassword")
                .param("passwordConfirmation", "testpassword");
        mockMvc.perform(testLogin)
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("Test Maintenance User2")))
                .andExpect(view().name("auth/register-success"));
    } */
}
