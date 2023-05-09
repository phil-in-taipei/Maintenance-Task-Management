package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.IntervalTaskGroupService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Controller
public class IntervalTaskGroupController {

    @Autowired
    IntervalTaskGroupService intervalTaskGroupService;

    @Autowired
    UserDetailsServiceImplementation userService;


    // directs user to form to apply interval task groups schedulers
    // to a specific quarter/year. It will also trigger batch scheduling
    // of the interval task schedulers in the group
    @PostMapping("/apply-interval-task-group-schedulers")
    public String showApplyITGSchedulerFormPage(
            @ModelAttribute("monthlyTaskQuarterAndYear")
            SearchQuarterAndYear monthlyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

      intervalTaskGroupService.getAllUsersIntervalTaskGroups(user.getUsername()); //user.getId()
        List<IntervalTaskGroup> availableIntervalTaskGroups =
                intervalTaskGroupService
                        .getAllUsersIntervalTaskGroupsAvailableForQuarterAndYear(
                                user.getUsername(), QuarterlySchedulingEnum.valueOf( // user.getId()
                                    monthlyTaskQuarterAndYear.getQuarter()),
                            monthlyTaskQuarterAndYear.getYear()
                );

        model.addAttribute("intervalTaskGroups", availableIntervalTaskGroups);
        model.addAttribute("user", user);
        model.addAttribute("quarter", monthlyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", monthlyTaskQuarterAndYear.getYear());
        IntervalTaskGroupAppliedQuarterly qITG = new IntervalTaskGroupAppliedQuarterly();
        model.addAttribute("qITG", qITG);
        return "tasks/apply-interval-task-group-schedulers";
    }

    // this is to create an interval task group
    @GetMapping("/create-interval-task-group")
    public String showCreateIntervalTaskGroupFormPage(Model model) {
        IntervalTaskGroup intervalTaskGroup = new IntervalTaskGroup();
        model.addAttribute("intervalTaskGroup", intervalTaskGroup);
        return "tasks/create-interval-task-group";
    }

    // this will produce a form to create an interval task scheduler which
    // will be one member of the interval task group. It will be used as a template
    // to schedule tasks when the interval task group is applied to a specific quarter/year
    @GetMapping("/create-interval-task/{taskGroupId}")
    public String showCreateIntervalTaskFormPage(
            @PathVariable(name = "taskGroupId") Long taskGroupId, Model model) {
        model.addAttribute("intervalTask", new IntervalTask());
        model.addAttribute("taskGroupId", taskGroupId);
        return "tasks/create-interval-task";
    }

    // link to delete an interval task (just one member of the task group)
    // it will verify that both the interval task and interval task group exists
    // prior to deletion; otherwise, it will redirect to an error page
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

    // Link to delete an interval task group interval task group exists
    // prior to deletion; otherwise, it will redirect to an error page.
    // It will also trigger the deletion of interval tasks in the group
    // by means of the orphanRemoval setting in the model
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

    // this will delete the record of the application of the interval task group
    // from the database. It will first verify that record with the corresponding id
    // exists prior to deletion; otherwise, link to an error page
    @RequestMapping("/delete-interval-task-group-applied-quarterly/{id}")
    public String deleteIntervalTaskGroupAppliedQuarterly(
            @PathVariable(name = "id") Long id, Model model) {
        if (intervalTaskGroupService.getIntervalTaskGroupAppliedQuarterly(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, interval task group" +
                            " applied quarterly with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        intervalTaskGroupService.deleteIntervalTaskGroupAppliedQuarterly(id);
        return "redirect:/quarterly-interval-task-groups-scheduled";
    }

    // link to submit the form for user to create a new interval task group
    // returns link to error page in the event creation fails
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
            return "error/error";
        }
        return "redirect:/interval-task-groups";
    }

    // this links to a page with a table showing all users' interval task groups
    // the member interval tasks are accessible via link on the page
    @GetMapping("/interval-task-groups")
    public String showAllUserIntervalTaskGroups(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<IntervalTaskGroup> intervalTaskGroups  =
                intervalTaskGroupService.getAllUsersIntervalTaskGroups(user.getUsername()); //
        model.addAttribute("intervalTaskGroups", intervalTaskGroups);
        model.addAttribute("user", user);
        model.addAttribute("intervalTaskQuarterAndYear",
                new SearchQuarterAndYear());
        return "tasks/interval-task-groups";
    }

    // link to save new interval task to a specific interval task group (according to id)
    @PostMapping("/interval-task-group/{taskGroupId}")
    public String saveNewIntervalTask(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            @ModelAttribute("intervalTask")
            IntervalTask intervalTask, Model model
            ) throws IllegalArgumentException {
        try {
            IntervalTaskGroup intervalTaskGroup =
                    intervalTaskGroupService.getIntervalTaskGroup(taskGroupId);
            if (intervalTaskGroup == null) {
                // if the id does not exist, it is redirected to an error page with a message
                model.addAttribute(
                        "message",
                        "Could not save interval task, Group ID: "
                                + taskGroupId + " does not exist.");
                return "error/error";
            } else {
                List<IntervalTask> intervalTasks = intervalTaskGroup.getIntervalTasks();
                //intervalTask.setNoRainOnly(false);
                intervalTasks.add(intervalTask);
                intervalTaskGroup.setIntervalTasks(intervalTasks);
                intervalTaskGroupService.saveIntervalTaskGroup(intervalTaskGroup);
            }
        } catch (IllegalArgumentException e) {
            // this is a general catch-all for errors
            model.addAttribute(
                    "message",
                    "Could not save interval task, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/interval-task-groups";
    }

    // this shows the interval task group and enables user to access the individual
    // interval tasks in the group as well as a link to create new interval tasks
    // in the group or delete the interval tasks in the group
    @GetMapping("/interval-task-group/{taskGroupId}")
    public ModelAndView showIntervalTaskGroup(
            @PathVariable(name = "taskGroupId") Long taskGroupId,
            Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        ModelAndView mav = new ModelAndView("tasks/interval-task-group");
        IntervalTaskGroup intervalTaskGroup =
                intervalTaskGroupService.getIntervalTaskGroup(taskGroupId);
        //System.out.println("This is the interval task group: " + intervalTaskGroup);
        if (intervalTaskGroup == null) {
            mav.setViewName("error/error");
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

    // this produces a table showing a record of all applications of interval task groups
    // to specific quarters/years
    @GetMapping("/quarterly-interval-task-groups-scheduled")
    public String showAllUserQuarterlyIntervalTaskGroups(
            Authentication authentication, Model model) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<IntervalTaskGroupAppliedQuarterly> qITG =
                intervalTaskGroupService.getAllUsersIntervalTaskGroupsAppliedQuarterly(user.getId());
        model.addAttribute("qITG", qITG);
        model.addAttribute("user", user);
        return "tasks/quarterly-interval-task-groups-scheduled";
    }

    // In the form the user selects the interval task group along with the year and quarter
    // In the service method, the scheduling of the sequence of tasks in the group will be
    // triggered for the quarter/year
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
