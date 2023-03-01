package MaintenanceManager.MaintenanceManager.controllers.tasks;

import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.MaintenanceTaskReschedule;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.MaintenanceTaskSubmit;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchTasksByDate;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.IntervalTaskGroupService;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
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

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;

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
        return "tasks/create-single-task";
    }

    @GetMapping("/reschedule-task/{taskId}")
    public ModelAndView showEditTaskPage(@PathVariable(name = "taskId") Long taskId) {
        ModelAndView mav = new ModelAndView("tasks/reschedule-task");
        MaintenanceTask originalTask = maintenanceTaskService.getMaintenanceTask(taskId);
        if (originalTask == null) {
            mav.setViewName("error/error");
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

    @PostMapping("/search-tasks-by-date")
    public String searchTasksByDate(
            @ModelAttribute("rescheduledTask")
            SearchTasksByDate searchTasksByDate,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        LocalDate queryDate = LocalDate.parse(searchTasksByDate.getDate());
        System.out.println("This is the query date: " + queryDate);
        List<MaintenanceTask> tasks = maintenanceTaskService.getAllUserTasksByDate(user.getId(), queryDate);
        System.out.println(tasks.toString());
        model.addAttribute("tasks", tasks);
        LocalDate dayBefore = queryDate.minusDays(1);
        LocalDate dayAfter = queryDate.plusDays(1);
        model.addAttribute("dayAfter", dayAfter.toString());
        model.addAttribute("dayBefore", dayBefore.toString());
        model.addAttribute("date", queryDate.toString()); //searchTasksByDate.getDate()
        model.addAttribute("user", user);
        return "tasks/tasks-by-date";
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
            return "error/error";
        }
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String showAllUserTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MaintenanceTask> tasks = maintenanceTaskService.getAllUserTasks(user.getId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks/tasks";
    }

    @GetMapping("tasks-by-date/{date}")
    public String showUserTasksByDate (
            @PathVariable(name = "date") String date,
            Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        LocalDate queryDate = LocalDate.parse(date);
        System.out.println("This is the query date: " + queryDate);
        List<MaintenanceTask> tasks = maintenanceTaskService.getAllUserTasksByDate(user.getId(), queryDate);
        System.out.println(tasks.toString());
        LocalDate dayBefore = queryDate.minusDays(1);
        LocalDate dayAfter = queryDate.plusDays(1);
        model.addAttribute("dayAfter", dayAfter.toString());
        model.addAttribute("dayBefore", dayBefore.toString());
        model.addAttribute("tasks", tasks);
        model.addAttribute("date", queryDate);
        model.addAttribute("user", user);
        return "tasks/tasks-by-date";
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
            return "error/error";
        } else {
            try {
                LocalDate newDate = LocalDate.parse(taskRescheduleForm.getDate());
                updatedTask.setDate(newDate);
                updatedTask.setStatus(TaskStatusEnum.DEFERRED);
                updatedTask.setComments(taskRescheduleForm.getComments());
                updatedTask.setUpdatedDateTime(LocalDateTime.now());
                updatedTask.setTimesModified(updatedTask.getTimesModified() + 1);
                maintenanceTaskService.saveTask(updatedTask);
            } catch (IllegalArgumentException e) {
                model.addAttribute(
                        "message",
                        "Could not update task, "
                                + e.getMessage());
                return "error/error";
            }
        }
        return "redirect:/tasks";
    }
}
