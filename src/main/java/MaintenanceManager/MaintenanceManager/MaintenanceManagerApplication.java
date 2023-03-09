package MaintenanceManager.MaintenanceManager;
import MaintenanceManager.MaintenanceManager.models.user.Authority;
import MaintenanceManager.MaintenanceManager.models.user.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.user.UserMeta;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.user.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.user.UserPrincipalRepo;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateDatesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

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
		SpringApplication
				.run(MaintenanceManagerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Override
	public void run(String... args) throws Exception {

		if (authorityRepo.findAll().isEmpty()) {
			// constructs the Authorities for the different user types and
			// saves to database. This is a key step before creating any users
			Authority userAuth = Authority.builder().id(1L)
					.authority(AuthorityEnum.ROLE_USER).build();
			Authority adminAuth = Authority.builder().id(2L)
					.authority(AuthorityEnum.ROLE_ADMIN).build();
			Authority mainAuth = Authority.builder().id(3L)
					.authority(AuthorityEnum.ROLE_MAINTENANCE).build();
			authorityRepo.saveAll(Arrays.asList(userAuth, adminAuth, mainAuth));
		}
	}

}
