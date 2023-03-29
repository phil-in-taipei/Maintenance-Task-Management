package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.MaintenanceManagerApplication;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MaintenanceManagerApplication.class)
@ActiveProfiles("test")
public class MaintenanceTaskServiceUnitTest {

    @MockBean
    MaintenanceTaskRepo maintenanceTaskRepo;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @MockBean
    UserDetailsServiceImplementation userService;

    /*
    @BeforeEach
    void init() {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setAge(20);
        userRegistration.setEmail("testemail@gmx.com");
        userRegistration.setPassword("testpassword");
        userRegistration.setPasswordConfirmation("testpassword");
        userRegistration.setGivenName("Test");
        userRegistration.setSurname("User");
        userRegistration.setUsername("Test Maintenance User");

        UserPrincipal userPrincipal = userService.createNewMaintenanceUser(
                userRegistration
        );
    } */

    /*
    @Test
    public void testGetMaintenanceTaskSuccessBehavior() throws Exception {
        MaintenanceTask maintenanceTask = MaintenanceTask.builder()
                .id(1L)
                .taskName("Test Task 1")
                .date(LocalDate.now())
                .user(
                .ingredients(Set.of(
                        Ingredient.builder().amount("1").name("pasta").build(),
                        Ingredient.builder().amount("1").name("pasta sauce").build())
                )
                .steps(Set.of(Step.builder()
                        .description("Boil water").stepNumber(1)
                        .description("Add and cook pasta").stepNumber(2)
                        .description("Drain water from pasta").stepNumber(3)
                        .description("Add sauce").stepNumber(4)
                        .description("Enjoy!")
                        .build()))
                .build();

        when(recipeRepo.findById(anyLong())).thenReturn(Optional.of(pasta));

        assertThat(recipeService.getRecipeById(1L)).isEqualTo(pasta);
    } */
}
