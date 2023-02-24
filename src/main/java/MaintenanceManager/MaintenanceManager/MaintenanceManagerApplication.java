package MaintenanceManager.MaintenanceManager;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import java.time.LocalDate;

import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.GenerateDatesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MaintenanceManagerApplication implements CommandLineRunner {

	@Autowired
	GenerateDatesService generateDatesService;

	@Autowired
	private AuthorityRepo authorityRepo;

	@Autowired
	private UserPrincipalRepo userPrincipalRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(MaintenanceManagerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("--------------------------------------------------------------------------------");
		List<LocalDate> dates = generateDatesService.getWeeklySchedulingDatesByQuarter(
				DayOfWeek.WEDNESDAY, 2023, QuarterlySchedulingEnum.Q1);
		System.out.println(dates.toString());

		dates = generateDatesService.getWeeklySchedulingDatesByQuarter(
				DayOfWeek.FRIDAY, 2023, QuarterlySchedulingEnum.Q2);
		System.out.println(dates.toString());

		dates = generateDatesService.getWeeklySchedulingDatesByQuarter(
				DayOfWeek.SUNDAY, 2023, QuarterlySchedulingEnum.Q3);
		System.out.println(dates.toString());

		dates = generateDatesService.getWeeklySchedulingDatesByQuarter(
				DayOfWeek.MONDAY, 2023, QuarterlySchedulingEnum.Q4);
		System.out.println(dates.toString());
		System.out.println("--------------------------------------------------------------------------------");


		Authority userAuth = Authority.builder().authority(AuthorityEnum.ROLE_USER).build();
		Authority adminAuth = Authority.builder().authority(AuthorityEnum.ROLE_ADMIN).build();
		Authority mainAuth = Authority.builder().authority(AuthorityEnum.ROLE_MAINTENANCE).build();

		if (authorityRepo.findAll().isEmpty()) {
			authorityRepo.saveAll(Arrays.asList(userAuth, adminAuth, mainAuth));
		}

		if (userPrincipalRepo.findAll().isEmpty()) {
			UserMeta admin = UserMeta.builder()
					.surname("Admin")
					.givenName("User")
					.email("admin@email.com")
					.age(35)
					.build();
			UserMeta main1 = UserMeta.builder()
					.surname("Maintenance1")
					.givenName("One")
					.email("maintenance1@email.com")
					.age(25)
					.build();
			userPrincipalRepo.saveAll(
					Arrays.asList(
							new UserPrincipal("MAIN1",
									passwordEncoder.encode("testpassword"),
									Arrays.asList(userAuth, mainAuth), main1),
							new UserPrincipal("ADMIN",
									passwordEncoder.encode("testpassword"),
									Arrays.asList(adminAuth, userAuth), admin)
					)
			);
		}
	}

}
