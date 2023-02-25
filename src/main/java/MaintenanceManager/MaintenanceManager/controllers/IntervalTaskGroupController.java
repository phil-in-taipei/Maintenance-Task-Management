package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.IntervalTaskQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.IntervalTaskGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Controller
public class IntervalTaskGroupController {

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;


    @GetMapping("/create-interval-task-group")
    public String showCreateIntervalTaskGroupFormPage(Model model) {
        IntervalTaskGroup intervalTaskGroup = new IntervalTaskGroup();
        model.addAttribute("intervalTaskGroup", intervalTaskGroup);
        return "create-interval-task-group";
    }

    @PostMapping("/interval-task-groups")
    public String saveNewIntervalTaskGroup(
            @ModelAttribute("intervalTaskGroup")
            IntervalTaskGroup intervalTaskGroup, Model model,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            intervalTaskGroup.setTaskGroupOwner(user);
            intervalTaskGroupService.saveIntervalTaskGroup(intervalTaskGroup);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save interval task group, "
                            + e.getMessage());
            return "error";
        }
        return "redirect:/interval-task-groups";
    }

    @GetMapping("/interval-task-groups")
    public String showAllUserIntervalTaskGroups(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<IntervalTaskGroup> intervalTaskGroups  =
                intervalTaskGroupService.getAllUsersIntervalTaskGroups(user.getId());
        model.addAttribute("intervalTaskGroups", intervalTaskGroups);
        model.addAttribute("user", user);
        model.addAttribute("intervalTaskQuarterAndYear",
                new IntervalTaskQuarterAndYear());
        return "interval-task-groups";
    }
}
