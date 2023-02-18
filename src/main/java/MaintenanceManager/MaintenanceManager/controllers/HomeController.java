package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.SearchTasksByDate;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.MaintenanceTaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/landing")
    public String landingPage(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the user: " + user);
        //List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasks(user.getId());
        List<MaintenanceTask> uncompletedTasks = maintenanceTaskService.getAllUncompletedPastUserTasks(user.getId());
        List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasksByDate(
                user.getId(), LocalDate.now());
        System.out.println("These are the user's tasks: " + maintenanceTasks.toString());
        SearchTasksByDate searchTasksByDate = new SearchTasksByDate();
        model.addAttribute("searchTasksByDate", searchTasksByDate);
        model.addAttribute("uncompletedTasks", uncompletedTasks);
        model.addAttribute("dailyTasks", maintenanceTasks);
        model.addAttribute("user", user);
        return "landing";
    }
}
