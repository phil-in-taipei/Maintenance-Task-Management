package MaintenanceManager.MaintenanceManager.controllerEndpoints.auth;
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

    // this renders the page with the form for maintenance users to register
    // those registering on this page will have maintenance authority
    // this link is shown in the navbar. Most users will register here
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "auth/register";
    }

    // this is the link to submit the form for maintenance users to register
    // those registering on this page will have maintenance authority
    @PostMapping("/register")
    public String submitRegisterForm(
            @ModelAttribute("userRegistration")
            UserRegistration userRegistration,
            Model model) {
        // verifies that the passwords match prior to registration;
        // otherwise, redirects to registration failure page
        if (!Objects.equals(userRegistration.getPassword(),
                userRegistration.getPasswordConfirmation())) {
            model.addAttribute("errorMsg", "The passwords do not match!");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService.createNewMaintenanceUser(userRegistration);
        // verifies user creation successful; otherwise, redirects to registration failure page
        if (createdUser == null) {
            model.addAttribute("errorMsg",
                    "There was error creating your account");
            return "register-failure";
        }
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }

    // this renders the page with the form for admin users to register
    // those registering on this page will have admin authority
    // this link is not shown in the navbar
    @GetMapping("/register-admin")
    public String showRegisterFormForAdmin(Model model) {
        model.addAttribute("userRegistration", new UserRegistration());
        return "auth/register-admin";
    }

    // this is the link to submit the form for admin users to register
    // those registering on this page will have admin authority
    @PostMapping("/register-admin")
    public String submitRegisterFormForAdmin(
            @ModelAttribute("userRegistration") UserRegistration userRegistration,
                                     Model model) {
        // verifies that the passwords match prior to registration;
        // otherwise, redirects to registration failure page
        if (!Objects.equals(userRegistration.getPassword(),
                userRegistration.getPasswordConfirmation())) {
            model.addAttribute("errorMsg",
                    "The passwords do not match!");
            return "auth/register-failure";
        }
        UserPrincipal createdUser = userDetailsService
                .createNewAdminUser(userRegistration);
        // verifies user creation successful; otherwise, redirects to registration failure page
        if (createdUser == null) {
            model.addAttribute("errorMsg",
                    "There was error creating your account");
            return "register-failure";
        }
        model.addAttribute("user", createdUser);
        return "auth/register-success";
    }
}
