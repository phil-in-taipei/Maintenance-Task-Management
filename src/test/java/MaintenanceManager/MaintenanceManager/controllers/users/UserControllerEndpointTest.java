package MaintenanceManager.MaintenanceManager.controllers.users;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
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
public class UserControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    @WithUserDetails("Test Admin User1") // this page is only accessible by admin users
    public void testShowAllMaintenanceUsersPage() throws Exception {
        mockMvc
                .perform(get("/maintenance-users"))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(status().is2xxSuccessful())
                //.andDo(print())
                // this are the expected model attribute
                .andExpect(model().attributeExists("maintenanceUsers"))
                // this substring is the name of the user created in the
                // MaintenanceManagerApplicationTest bootstrapping class.
                // This makes sure that the expected user is
                // displayed on the page
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString("Test Maintenance User1")))
                .andExpect(view().name("users/maintenance-users"));
    }

    @Test
    @Order(2)
    @WithUserDetails("Test Maintenance User1") // this page is only accessible to admin users
    public void testShowAllMaintenanceUsersPageUnauthorizedWrongUserType()
            throws Exception {
        mockMvc
                .perform(get("/maintenance-users"))
                .andExpect(status().isForbidden());
    }
}
