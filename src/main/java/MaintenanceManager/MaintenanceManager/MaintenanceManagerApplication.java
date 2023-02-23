package MaintenanceManager.MaintenanceManager;

import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class MaintenanceManagerApplication implements CommandLineRunner {

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
		Authority userAuth = Authority.builder().authority(AuthorityEnum.ROLE_USER).build();
		Authority adminAuth = Authority.builder().authority(AuthorityEnum.ROLE_ADMIN).build();
		Authority mainAuth = Authority.builder().authority(AuthorityEnum.ROLE_MAINTENANCE).build();

		LocalDate ld = LocalDate.of(2023, 1, 1);
		System.out.println("*************First day of the year: " + ld + "***************************");
		System.out.println("*********************************First Sunday of the year: " +
				ld.with(DayOfWeek.SUNDAY) + "******************************");
		ld = LocalDate.of(2023, 1, 2);
		System.out.println("*********************************First Monday of the year: " +
				ld.with(DayOfWeek.MONDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Tuesday of the year: " +
				ld.with(DayOfWeek.TUESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Wednesday of the year: " +
				ld.with(DayOfWeek.WEDNESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Thursday of the year: " +
				ld.with(DayOfWeek.THURSDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Friday of the year: " +
				ld.with(DayOfWeek.FRIDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Saturday of the year: " +
				ld.with(DayOfWeek.SATURDAY) + "******************************");
		System.out.println("*************************************************************");
		LocalDate ldQ2 = LocalDate.of(2023, 4, 1);
		System.out.println("*************First day of the year: " + ld + "***************************");
		System.out.println("*********************************First Sunday of the year: " +
				ldQ2.with(DayOfWeek.SUNDAY) + "******************************");
		ldQ2 = LocalDate.of(2023, 4, 2);
		System.out.println("*********************************First Monday of the Q: " +
				ldQ2.with(DayOfWeek.MONDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Tuesday of the Q: " +
				ldQ2.with(DayOfWeek.TUESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Wednesday of the Q: " +
				ldQ2.with(DayOfWeek.WEDNESDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Thursday of the Q: " +
				ldQ2.with(DayOfWeek.THURSDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Friday of the Q: " +
				ldQ2.with(DayOfWeek.FRIDAY) + "******************************");
		//ld = LocalDate.of(2023, 1, 1);
		System.out.println("*********************************First Saturday of the Q: " +
				ldQ2.with(DayOfWeek.SATURDAY) + "******************************");
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
