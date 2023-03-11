package MaintenanceManager.MaintenanceManager.controllers.users;

import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @GetMapping("/maintenance-users")
    public String showAllMaintenanceUsersPage(Model model) {
        List<UserPrincipal> maintenanceUsers = userDetailsService.getAllMaintenanceUsers();
        model.addAttribute("maintenanceUsers", maintenanceUsers);
        return "users/maintenance-users";
    }
}
