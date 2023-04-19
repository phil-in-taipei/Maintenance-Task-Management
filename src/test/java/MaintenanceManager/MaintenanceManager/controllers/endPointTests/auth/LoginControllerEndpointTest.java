package MaintenanceManager.MaintenanceManager.controllers.endPointTests.auth;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import org.junit.jupiter.api.*;
import org.springframework.security.test.context.support.WithUserDetails;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
public class LoginControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    // test endpoint to display login form
    @Test
    @Order(1)
    public void testShowLoginPage() throws Exception {
        mockMvc
                .perform(get("/login"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("username")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("password")))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsStringIgnoringCase("login")))
                .andExpect(view().name("auth/login"));
    }

    // this passes the correct username of a user created in the
    // MaintenanceManagerApplicationTest bootstrapping class with
    // an incorrect password to make sure it redirects back with error
    @Test
    @Order(3)
    public void testLoginFailure() throws Exception {
        MockHttpServletRequestBuilder testLogin = post(
                "/login")
                .param("username", "Test Maintenance User1")
                .param("password", "incorrect");
        mockMvc.perform(testLogin)
                //.andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    // this passes the correct username of a user created in the
    // MaintenanceManagerApplicationTest bootstrapping class with
    // a correct password to make sure it logs in and redirect to home page
    @Test
    @Order(2)
    public void testLoginSuccess() throws Exception {
        MockHttpServletRequestBuilder testLogin = post(
                "/login")
                .param("username", "Test Maintenance User1")
                .param("password", "testpassword");
        mockMvc.perform(testLogin)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/landing"));
    }

    @Test
    @Order(4)
    @WithUserDetails("Test Maintenance User1")
    public void testLogoutSuccess() throws Exception {
        mockMvc
                .perform(post("/logout"))
                //.andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}
