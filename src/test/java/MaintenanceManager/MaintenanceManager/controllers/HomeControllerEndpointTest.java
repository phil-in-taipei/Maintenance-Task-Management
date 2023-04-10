package MaintenanceManager.MaintenanceManager.controllers;

import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HomeControllerEndpointTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void baseURLShouldReturnIndexNoWeatherView() throws Exception {
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
    public void incorrectURLShouldReturnErrorStatus() throws Exception {
        mockMvc
                .perform(get("/sdlsajsadd")) // a random incorrect string
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails("Test Maintenance User1")
    public void landingURLShouldReturnLandingNoWeatherModelsAndView() throws Exception {
        mockMvc
                .perform(get("/landing"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("uncompletedTasks"))
                .andExpect(model().attributeExists("dailyTasks"))
                .andExpect(view().name("landing-no-weather"));
    }

    @Test
    public void landingURLShouldThrowUnauthorizedErrorForUnauthenticatedUser()
            throws Exception {
        mockMvc
                .perform(get("/landing"))
                .andExpect(status().isUnauthorized());
    }
}
