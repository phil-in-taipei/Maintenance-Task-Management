package MaintenanceManager.MaintenanceManager.controllers;

import MaintenanceManager.MaintenanceManager.models.*;
import MaintenanceManager.MaintenanceManager.services.MaintenanceTaskService;
import org.jboss.jandex.Main;
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
public class MaintenanceTaskController {

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @RequestMapping("/confirm-task-completion/{taskId}")
    public String confirmTaskCompletion(@PathVariable(name = "taskId") Long taskId) {
        MaintenanceTask task = maintenanceTaskService.getMaintenanceTask(taskId);
        if (task != null) {
            maintenanceTaskService.confirmTaskCompletion(task);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/create-single-task")
    public String showSubmitTaskFormPage(Model model) {
        MaintenanceTaskSubmit maintenanceTask = new MaintenanceTaskSubmit();
        model.addAttribute("maintenanceTask", maintenanceTask);
        return "create-single-task";
    }

    @GetMapping("/reschedule-task/{taskId}")
    public ModelAndView showEditTaskPage(@PathVariable(name = "taskId") Long taskId) {
        ModelAndView mav = new ModelAndView("reschedule-task");
        MaintenanceTask originalTask = maintenanceTaskService.getMaintenanceTask(taskId);
        if (originalTask == null) {
            mav.setViewName("error");
            mav.addObject("message",
                    "Task with id "
                            + taskId + " does not exist."
            );
        } else {
            MaintenanceTaskReschedule editTaskForm = new MaintenanceTaskReschedule();
            mav.addObject("task", originalTask);
            mav.addObject("editTaskForm", editTaskForm);
        }
        return mav;
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

    @GetMapping("/tasks")
    public String showAllUserTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MaintenanceTask> tasks = maintenanceTaskService.getAllUserTasks(user.getId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks";
    }

    @PostMapping("/submit-reschedule-task-form/{taskId}")
    public String rescheduleTask(
            @PathVariable(name = "taskId") Long taskId,
            @ModelAttribute("rescheduledTask")
            MaintenanceTaskReschedule taskRescheduleForm,
            Model model) {

        MaintenanceTask updatedTask = maintenanceTaskService.getMaintenanceTask(
                taskId);
        if (updatedTask == null) {
            model.addAttribute("message",
                    "Cannot update, task does not exist!");
            return "error";
        } else {
            try {
                LocalDate newDate = LocalDate.parse(taskRescheduleForm.getDate());
                updatedTask.setDate(newDate);
                updatedTask.getTaskStatusHistory().setStatus(TaskStatusEnum.DEFERRED);
                updatedTask.getTaskStatusHistory().setComments(taskRescheduleForm.getComments());
                updatedTask.getTaskStatusHistory().setUpdatedDateTime(LocalDateTime.now());
                updatedTask.getTaskStatusHistory().setTimesModified(
                        updatedTask.getTaskStatusHistory().getTimesModified() + 1);
                maintenanceTaskService.saveTask(updatedTask);
            } catch (IllegalArgumentException e) {
                model.addAttribute(
                        "message",
                        "Could not update task, "
                                + e.getMessage());
                return "error";
            }
        }
        return "redirect:/tasks";
    }
}
