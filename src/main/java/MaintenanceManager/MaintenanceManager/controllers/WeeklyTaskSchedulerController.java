package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.WeeklyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.WeeklyTaskQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.WeeklyTaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Controller
public class WeeklyTaskSchedulerController {

    @Autowired
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

    @GetMapping("/create-weekly-task-scheduler")
    public String showCreateWeeklyTaskFormPage(Model model) {
        WeeklyTaskScheduler weeklyTaskScheduler = new WeeklyTaskScheduler();
        DayOfWeek[] daysOfWeekOptions = DayOfWeek.values();
        model.addAttribute("daysOfWeekOptions", daysOfWeekOptions);
        model.addAttribute("weeklyTaskScheduler", weeklyTaskScheduler);
        return "create-weekly-task-scheduler";
    }

    @PostMapping("/weekly-tasks")
    public String saveNewWeeklyTaskScheduler(
            @ModelAttribute("weeklyTaskScheduler")
            WeeklyTaskScheduler weeklyTaskScheduler, Model model,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            weeklyTaskScheduler.setUser(user);
            weeklyTaskSchedulingService.saveWeeklyTaskScheduler(weeklyTaskScheduler);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save weekly task scheduler, "
                            + e.getMessage());
            return "error";
        }
        return "redirect:/weekly-tasks";
    }

    @GetMapping("/weekly-tasks")
    public String showAllUserWeeklyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<WeeklyTaskScheduler> weeklyTasks =
                weeklyTaskSchedulingService.getAllUsersWeeklyTaskSchedulers(user.getId());

        model.addAttribute("weeklyTasks", weeklyTasks);
        model.addAttribute("user", user);
        model.addAttribute("weeklyTaskQuarterAndYear", new WeeklyTaskQuarterAndYear());
        return "weekly-task-schedulers";
    }
}
