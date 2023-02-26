package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTask;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.IntervalTaskQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.MaintenanceTaskSubmit;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.IntervalTaskGroupService;
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
public class IntervalTaskGroupController {

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;


    @GetMapping("/create-interval-task-group")
    public String showCreateIntervalTaskGroupFormPage(Model model) {
        IntervalTaskGroup intervalTaskGroup = new IntervalTaskGroup();
        model.addAttribute("intervalTaskGroup", intervalTaskGroup);
        return "create-interval-task-group";
    }

    @GetMapping("/create-interval-task/{taskGroupId}")
    public String showCreateIntervalTaskFormPage(
            @PathVariable(name = "taskGroupId") Long taskGroupId, Model model) {
        IntervalTask intervalTask = new IntervalTask();
        System.out.println("This is the interval task to add to the form: " + intervalTask);
        System.out.println("This is the task group id: " + taskGroupId);
        model.addAttribute("intervalTask", new IntervalTask());
        model.addAttribute("taskGroupId", taskGroupId);
        return "create-interval-task";
    }

    @RequestMapping("/delete-interval-task/{intervalTaskId}/{taskGroupId}")
    public String deleteIntervalTask(
            @PathVariable(name = "intervalTaskId")
            Long intervalTaskId,
            @PathVariable(name = "taskGroupId")
            Long taskGroupId,
            Model model) {
        if (intervalTaskGroupService.getIntervalTask(intervalTaskId) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task with id " + intervalTaskId + " does not exist.");
            return "error";
        }
        if (intervalTaskGroupService.getIntervalTaskGroup(taskGroupId) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task group with id " + taskGroupId + " does not exist.");
            return "error";
        }
        intervalTaskGroupService.deleteIntervalTask(intervalTaskId);
        return "redirect:/interval-task-group/" + taskGroupId;
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

    @PostMapping("/interval-task-group/{taskGroupId}")
    public String saveNewIntervalTask(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            @ModelAttribute("intervalTask")
            IntervalTask intervalTask, Model model
            ) {
        try {
            IntervalTaskGroup intervalTaskGroup =
                    intervalTaskGroupService.getIntervalTaskGroup(taskGroupId);
            List<IntervalTask> intervalTasks = intervalTaskGroup.getIntervalTasks();
            //intervalTask.setNoRainOnly(false);
            intervalTasks.add(intervalTask);
            intervalTaskGroup.setIntervalTasks(intervalTasks);
            intervalTaskGroupService.saveIntervalTaskGroup(intervalTaskGroup);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save interval task, "
                            + e.getMessage());
            return "error";
        }
        return "redirect:/interval-task-groups";
    }

    @GetMapping("/interval-task-group/{taskGroupId}")
    public ModelAndView showIntervalTaskGroup(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        ModelAndView mav = new ModelAndView("interval-task-group");
        IntervalTaskGroup intervalTaskGroup =
                intervalTaskGroupService.getIntervalTaskGroup(taskGroupId);
        System.out.println("This is the interval task group: " + intervalTaskGroup);
        if (intervalTaskGroup == null) {
            mav.setViewName("error");
            mav.addObject("message",
                    "Interval Task Group with id "
                            + taskGroupId + " does not exist."
            );
        } else {
            mav.addObject("intervalTaskGroup", intervalTaskGroup);
            mav.addObject("user", user);
        }
        return mav;
    }
}
