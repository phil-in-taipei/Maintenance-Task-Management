package MaintenanceManager.MaintenanceManager.controllers.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // displays login form page
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }


}
