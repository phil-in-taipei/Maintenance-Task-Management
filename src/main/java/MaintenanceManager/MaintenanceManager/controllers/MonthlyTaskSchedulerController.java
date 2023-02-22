package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.MaintenanceTaskSubmit;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.MonthlyTaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Controller
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @GetMapping("/create-monthly-task-scheduler")
    public String showSubmitTaskFormPage(Model model) {
        MonthlyTaskScheduler monthlyTaskScheduler = new MonthlyTaskScheduler();
        model.addAttribute("monthlyTaskScheduler", monthlyTaskScheduler);
        return "create-monthly-task-scheduler";
    }

    @PostMapping("/monthly-tasks")
    public String saveNewMonthlyTaskScheduler(
            @ModelAttribute("monthlyTaskScheduler")
            MonthlyTaskScheduler monthlyTaskScheduler, Model model,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            monthlyTaskScheduler.setUser(user);
            monthlyTaskSchedulingService.saveMonthlyTaskScheduler(monthlyTaskScheduler);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save monthly task scheduler, "
                            + e.getMessage());
            return "error";
        }
        return "redirect:/monthly-tasks";
    }

    @GetMapping("/monthly-tasks")
    public String showAllUserTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MonthlyTaskScheduler> monthlyTasks =
                monthlyTaskSchedulingService.getAllUsersMonthlyTaskSchedulers(user.getId());
        model.addAttribute("monthlyTasks", monthlyTasks);
        model.addAttribute("user", user);
        return "monthly-task-schedulers";
    }
}
