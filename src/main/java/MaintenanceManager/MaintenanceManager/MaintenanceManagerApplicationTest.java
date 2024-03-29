package MaintenanceManager.MaintenanceManager;


import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.repositories.tasks.*;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;

import java.util.Arrays;

@SpringBootApplication
@Profile("test")
public class MaintenanceManagerApplicationTest implements CommandLineRunner {

    @Autowired
    IntervalTaskAppliedQuarterlyRepo intervalTaskAppliedQuarterlyRepo;

    @Autowired
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    IntervalTaskRepo intervalTaskRepo;

    @Autowired
    MaintenanceTaskRepo maintenanceTaskRepo;

    @Autowired
    MonthlyTaskAppliedQuarterlyRepo monthlyTaskAppliedQuarterlyRepo;

    @Autowired
    MonthlyTaskSchedulerRepo monthlyTaskSchedulerRepo;

    @Autowired
    private UserPrincipalRepo userPrincipalRepo;

    @Autowired
    private UserDetailsServiceImplementation userService;

    @Autowired
    WeeklyTaskAppliedQuarterlyRepo weeklyTaskAppliedQuarterlyRepo;

    @Autowired
    WeeklyTaskSchedulerRepo weeklyTaskSchedulerRepo;

    public static void main(String[] args) {
        SpringApplication
                .run(MaintenanceManagerApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        intervalTaskAppliedQuarterlyRepo.deleteAll();
        intervalTaskGroupRepo.deleteAll();
        intervalTaskRepo.deleteAll();
        maintenanceTaskRepo.deleteAll(); // remove this line from here and put at
        // the end of the test classes
        monthlyTaskAppliedQuarterlyRepo.deleteAll(); // remove later
        monthlyTaskSchedulerRepo.deleteAll(); // remove this line from here and put at
        // the end of the test classes
        weeklyTaskAppliedQuarterlyRepo.deleteAll(); // remove later
        weeklyTaskSchedulerRepo.deleteAll(); // remove this line from here and put at
        // the end of the test classes

        if (userPrincipalRepo.findAll().isEmpty()) {
            System.out.println("The user repo is empty");
            UserRegistration maintenanceUserRegistration = new UserRegistration();
            maintenanceUserRegistration.setAge(20);
            maintenanceUserRegistration.setEmail("testemail@gmx.com");
            maintenanceUserRegistration.setPassword("testpassword");
            maintenanceUserRegistration.setPasswordConfirmation("testpassword");
            maintenanceUserRegistration.setGivenName("Test");
            maintenanceUserRegistration.setSurname("User1");
            maintenanceUserRegistration.setUsername("Test Maintenance User1");

            UserPrincipal maintenanceUser1 = userService.createNewMaintenanceUser(
                    maintenanceUserRegistration
            );

            UserRegistration adminUserRegistration = new UserRegistration();
            adminUserRegistration.setAge(30);
            adminUserRegistration.setEmail("testemail@gmx.com");
            adminUserRegistration.setPassword("testpassword");
            adminUserRegistration.setPasswordConfirmation("testpassword");
            adminUserRegistration.setGivenName("Test");
            adminUserRegistration.setSurname("Admin User");
            adminUserRegistration.setUsername("Test Admin User1");

            UserPrincipal adminUser1 = userService.createNewAdminUser(
                        adminUserRegistration);
        }
    }
}
