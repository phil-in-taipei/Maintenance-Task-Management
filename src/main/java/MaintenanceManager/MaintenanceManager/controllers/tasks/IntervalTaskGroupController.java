package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.IntervalTaskGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Controller
public class IntervalTaskGroupController {

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;

    @PostMapping("/apply-interval-task-group-schedulers")
    public String showApplyITGSchedulerFormPage(
            @ModelAttribute("monthlyTaskQuarterAndYear")
            SearchQuarterAndYear monthlyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the quarter: " + monthlyTaskQuarterAndYear.getQuarter());
        System.out.println("This is the year: " + monthlyTaskQuarterAndYear.getYear());
      intervalTaskGroupService.getAllUsersIntervalTaskGroups(user.getId());
        List<IntervalTaskGroup> availableIntervalTaskGroups =
                intervalTaskGroupService
                        .getAllUsersIntervalTaskGroupsAvailableForQuarterAndYear(
                            user.getId(), QuarterlySchedulingEnum.valueOf(
                                    monthlyTaskQuarterAndYear.getQuarter()),
                            monthlyTaskQuarterAndYear.getYear()
                );

        System.out.println(availableIntervalTaskGroups.toString());
        model.addAttribute("intervalTaskGroups", availableIntervalTaskGroups);
        model.addAttribute("user", user);
        model.addAttribute("quarter", monthlyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", monthlyTaskQuarterAndYear.getYear());
        IntervalTaskGroupAppliedQuarterly qITG = new IntervalTaskGroupAppliedQuarterly();
        model.addAttribute("qITG", qITG);
        return "tasks/apply-interval-task-group-schedulers";
    }

    @GetMapping("/create-interval-task-group")
    public String showCreateIntervalTaskGroupFormPage(Model model) {
        IntervalTaskGroup intervalTaskGroup = new IntervalTaskGroup();
        model.addAttribute("intervalTaskGroup", intervalTaskGroup);
        return "tasks/create-interval-task-group";
    }

    @GetMapping("/create-interval-task/{taskGroupId}")
    public String showCreateIntervalTaskFormPage(
            @PathVariable(name = "taskGroupId") Long taskGroupId, Model model) {
        IntervalTask intervalTask = new IntervalTask();
        System.out.println("This is the interval task to add to the form: " + intervalTask);
        System.out.println("This is the task group id: " + taskGroupId);
        model.addAttribute("intervalTask", new IntervalTask());
        model.addAttribute("taskGroupId", taskGroupId);
        return "tasks/create-interval-task";
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
                    "Cannot delete, interval task with id " +
                            intervalTaskId + " does not exist.");
            return "error/error";
        }
        if (intervalTaskGroupService.getIntervalTaskGroup(taskGroupId) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task group with id " +
                            taskGroupId + " does not exist.");
            return "error/error";
        }
        intervalTaskGroupService.deleteIntervalTask(intervalTaskId);
        return "redirect:/interval-task-group/" + taskGroupId;
    }

    @RequestMapping("/delete-interval-task-group/{id}")
    public String deleteIntervalTaskGroup(
            @PathVariable(name = "id") Long id, Model model) {
        if (intervalTaskGroupService.getIntervalTaskGroup(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task group with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        intervalTaskGroupService.deleteIntervalTaskGroup(id);
        return "redirect:/interval-task-groups";
    }

    @RequestMapping("/delete-interval-task-group-applied-quarterly/{id}")
    public String deleteIntervalTaskGroupAppliedQuarterly(
            @PathVariable(name = "id") Long id, Model model) {
        if (intervalTaskGroupService.getIntervalTaskGroupAppliedQuarterly(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task group " +
                            " applied quarterly with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        intervalTaskGroupService.deleteIntervalTaskGroupAppliedQuarterly(id);
        return "redirect:/quarterly-interval-task-groups-scheduled";
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
                new SearchQuarterAndYear());
        return "tasks/interval-task-groups";
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
            return "error/error";
        }
        return "redirect:/interval-task-groups";
    }

    @GetMapping("/interval-task-group/{taskGroupId}")
    public ModelAndView showIntervalTaskGroup(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        ModelAndView mav = new ModelAndView("tasks/interval-task-group");
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

    @GetMapping("/quarterly-interval-task-groups-scheduled")
    public String showAllUserQuarterlyIntervalTaskGroups(
            Authentication authentication, Model model) {
        System.out.println(
                "**********************************Controller method to get qITGs for the user***********************************");
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<IntervalTaskGroupAppliedQuarterly> qITG =
                intervalTaskGroupService.getAllUsersIntervalTaskGroupsAppliedQuarterly(user.getId());
        //System.out.println(
        //        "**********************************These are the qITGs for the user: " +
        //                qITG.toString() + "***********************************");
        model.addAttribute("qITG", qITG);
        model.addAttribute("user", user);
        return "tasks/quarterly-interval-task-groups-scheduled";
    }

    @PostMapping("/submit-quarterly-interval-task-group-scheduled/{quarter}/{year}")
    public String saveNewQuarterlyIntervalTaskGroup(
            @ModelAttribute("qITG")
            IntervalTaskGroupAppliedQuarterly qITG, Model model,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") Integer year,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            qITG.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qITG.setYear(year);
            intervalTaskGroupService.saveIntervalTaskGroupAppliedQuarterly(qITG);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save interval task group scheduler, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/quarterly-interval-task-groups-scheduled";
    }
}
