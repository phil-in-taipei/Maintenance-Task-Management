package MaintenanceManager.MaintenanceManager.controllers;

import MaintenanceManager.MaintenanceManager.models.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.MaintenanceTaskSubmit;
import MaintenanceManager.MaintenanceManager.models.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.MaintenanceTaskService;
import org.jboss.jandex.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
@Controller
public class MaintenanceTaskController {

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @GetMapping("/create-single-task")
    public String showSubmitBikeFormPage(Model model) {
        MaintenanceTask maintenanceTask = new MaintenanceTask();
        model.addAttribute("maintenanceTask", maintenanceTask);
        return "create-single-task";
    }


    @GetMapping("/tasks")
    public String showAllUserTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MaintenanceTask> tasks = maintenanceTaskService.getAllUserTasks(user.getId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks";
    }

    @PostMapping("/tasks")
    public String saveNewTask(
            @ModelAttribute("task")
            MaintenanceTaskSubmit maintenanceTaskForm, Model model,
            Authentication authentication) {
        try {
            LocalDate date = LocalDate.parse(maintenanceTaskForm.getDate());
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            MaintenanceTask maintenanceTask = new MaintenanceTask(
                    maintenanceTaskForm.getTaskName(),
                    maintenanceTaskForm.getDescription(),
                    date, user);
            maintenanceTaskService.saveTask(maintenanceTask);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save task, "
                            + e.getMessage());
            return "error";
        }
        return "redirect:/tasks";
    }
}
