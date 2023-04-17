package MaintenanceManager.MaintenanceManager.controllerEndpoints;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HomeControllerEndpointTest {
    @Autowired
    MockMvc mockMvc;

    // this currently shows the index-no-weather page instead
    // of index page because of api key expiration
    @Test
    public void testHomePage() throws Exception {
        mockMvc
                .perform(get("/"))
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index-no-weather"));
    }

    @Test
    @WithUserDetails("Test Maintenance User1")
    public void testIncorrectUrlError() throws Exception {
        mockMvc
                .perform(get("/sdlsajsadd")) // a random incorrect string
                .andExpect(status().is4xxClientError());
    }

    // this currently shows the landing-no-weather page instead of
    // landing page because of api key expiration
    @Test
    @WithUserDetails("Test Maintenance User1")
    public void testLandingPageMaintenanceUser() throws Exception {
        mockMvc
                .perform(get("/landing"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("uncompletedTasks"))
                .andExpect(model().attributeExists("dailyTasks"))
                // this substring is the name of the user created in the
                // MaintenanceManagerApplicationTest bootstrapping class.
                // This makes sure that the expected user is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Maintenance User1")))
                .andExpect(view().name("landing-no-weather"));
    }

    // tests that unauthenticated users cannot access the landing page
    @Test
    public void testLandingPageErrorForUnauthenticatedUser()
            throws Exception {
        mockMvc
                .perform(get("/landing"))
                .andExpect(status().isUnauthorized());
    }
}
