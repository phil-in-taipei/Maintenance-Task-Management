package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.UserRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    @GetMapping("/register")
    public String showRegisterForm() { //
        //model.addAttribute("userRegistration", new UserRegistration());
        return "register";
    }
    /*
    @PostMapping("/register")
    public String submitRegisterForm(@ModelAttribute("userRegistration") UserRegistration userRegistration,
                                     Model model) {
        System.out.println("*****************Submitting User Registration Data ************************");
        System.out.println(userRegistration.toString());
        //UserModel createdUser = userService.registerNewUser(user);
        //if (createdUser == null) {
        //    return "register-failure";
        //}
        //model.addAttribute("user", createdUser);
        return "register-success";
    }

     */
}
