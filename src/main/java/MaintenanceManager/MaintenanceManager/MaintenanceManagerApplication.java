package MaintenanceManager.MaintenanceManager;

import MaintenanceManager.MaintenanceManager.models.Authority;
import MaintenanceManager.MaintenanceManager.models.AuthorityEnum;
import MaintenanceManager.MaintenanceManager.models.UserMeta;
import MaintenanceManager.MaintenanceManager.models.UserPrincipal;
import MaintenanceManager.MaintenanceManager.repositories.AuthorityRepo;
import MaintenanceManager.MaintenanceManager.repositories.UserPrincipalRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

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

	@Override
	public void run(String... args) throws Exception {
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
