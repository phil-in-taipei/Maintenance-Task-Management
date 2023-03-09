package MaintenanceManager.MaintenanceManager.controllers.auth;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.user.UserRegistration;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class RegistrationController {

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "auth/register";
    }

    @PostMapping("/register")
    public String submitRegisterForm(@ModelAttribute("userRegistration") UserRegistration userRegistration,
                                     Model model) {
        System.out.println("*****************Submitting User Registration Data ************************");
        System.out.println(userRegistration.toString());
        if (!Objects.equals(userRegistration.getPassword(), userRegistration.getPasswordConfirmation())) {
            model.addAttribute("errorMsg", "The passwords do not match!");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService.createNewMaintenanceUser(userRegistration);
        System.out.println("**********************Created User****************************************");
        if (createdUser == null) {
            model.addAttribute("errorMsg", "There was error creating your account");
            return "register-failure";
        }
        System.out.println(createdUser.toString());
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }

    @GetMapping("/register-admin")
    public String showRegisterFormForAdmin(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "auth/register-admin";
    }

    @PostMapping("/register-admin")
    public String submitRegisterFormForAdmin(@ModelAttribute("userRegistration") UserRegistration userRegistration,
                                     Model model) {
        System.out.println("*****************Submitting User Registration Data ************************");
        System.out.println(userRegistration.toString());
        if (!Objects.equals(userRegistration.getPassword(), userRegistration.getPasswordConfirmation())) {
            model.addAttribute("errorMsg", "The passwords do not match!");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService.createNewAdminUser(userRegistration);
        System.out.println("**********************Created User****************************************");
        if (createdUser == null) {
            model.addAttribute("errorMsg", "There was error creating your account");
            return "register-failure";
        }
        System.out.println(createdUser.toString());
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }
}
