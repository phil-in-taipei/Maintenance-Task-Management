package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the user: " + user);
        model.addAttribute("user", user);
        return "index";
    }
}
