package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.WeeklyTaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
@Controller
public class WeeklyTaskSchedulerController {

    @Autowired
    WeeklyTaskSchedulingService weeklyTaskSchedulingService;

    // directs user to form to apply weekly schedulers
    // to a specific quarter/year. It will also trigger batch scheduling
    // of task on the specified day of week throughout the quarter
    @PostMapping("/apply-weekly-schedulers")
    public String showApplyWeeklySchedulerFormPage(
            @ModelAttribute("weeklyTaskQuarterAndYear")
            SearchQuarterAndYear weeklyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the quarter: " + weeklyTaskQuarterAndYear.getQuarter());
        System.out.println("This is the year: " + weeklyTaskQuarterAndYear.getYear());
        List<WeeklyTaskScheduler> availableWeeklyTasks =
                weeklyTaskSchedulingService
                    .getAllUsersWeeklyTaskSchedulersAvailableForQuarterAndYear(
                    user.getId(), QuarterlySchedulingEnum.valueOf(
                        weeklyTaskQuarterAndYear.getQuarter()),
                    weeklyTaskQuarterAndYear.getYear()
        );
        System.out.println(availableWeeklyTasks.toString());
        model.addAttribute("weeklyTasks", availableWeeklyTasks);
        model.addAttribute("user", user);
        model.addAttribute("quarter", weeklyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", weeklyTaskQuarterAndYear.getYear());
        WeeklyTaskAppliedQuarterly qWeeklyTask = new WeeklyTaskAppliedQuarterly();
        model.addAttribute("qWeeklyTask", qWeeklyTask);
        return "tasks/apply-weekly-schedulers";
    }

    // directs user to form for the creation of a weekly scheduler
    @GetMapping("/create-weekly-task-scheduler")
    public String showCreateWeeklyTaskFormPage(Model model) {
        WeeklyTaskScheduler weeklyTaskScheduler = new WeeklyTaskScheduler();
        DayOfWeek[] daysOfWeekOptions = DayOfWeek.values();
        model.addAttribute("daysOfWeekOptions", daysOfWeekOptions);
        model.addAttribute("weeklyTaskScheduler", weeklyTaskScheduler);
        return "tasks/create-weekly-task-scheduler";
    }

    // link to delete weekly scheduler. Returns an error if the id does not match
    // a weekly scheduler (does not exist)
    @RequestMapping("/delete-weekly-task-scheduler/{id}")
    public String deleteWeeklyMaintenanceTask(
            @PathVariable(name = "id") Long id, Model model) {
        if (weeklyTaskSchedulingService.getWeeklyTaskScheduler(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, monthly task with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        weeklyTaskSchedulingService.deleteWeeklyTaskScheduler(id);
        return "redirect:/weekly-tasks";
    }

    // link to delete quarterly applied record of monthly scheduler.
    // Returns an error if the id does not match an object in the database (does not exist)
    @RequestMapping("/delete-weekly-task-applied-quarterly/{id}")
    public String deleteWeeklyTaskAppliedQuarterly(
            @PathVariable(name = "id") Long id, Model model) {
        if (weeklyTaskSchedulingService.getWeeklyTaskAppliedQuarterly(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, monthly task applied quarterly with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        weeklyTaskSchedulingService.deleteWeeklyTaskAppliedQuarterly(id);
        return "redirect:/quarterly-weekly-tasks-scheduled";
    }

    // link for posting request to create new weekly task scheduler
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
            return "error/error";
        }
        return "redirect:/weekly-tasks";
    }

    // shows all weekly task schedulers which have been created by the authenticated user
    @GetMapping("/weekly-tasks")
    public String showAllUserWeeklyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<WeeklyTaskScheduler> weeklyTasks =
                weeklyTaskSchedulingService.getAllUsersWeeklyTaskSchedulers(user.getId());

        model.addAttribute("weeklyTasks", weeklyTasks);
        model.addAttribute("user", user);
        model.addAttribute("weeklyTaskQuarterAndYear", new SearchQuarterAndYear());
        return "tasks/weekly-task-schedulers";
    }

    // shows record of all quarterly/yearly application of weekly task schedulers
    // these correspond to the scheduling of the specified weekly task on the day
    // of the week throughout the quarter
    @GetMapping("/quarterly-weekly-tasks-scheduled")
    public String showAllUserQuarterlyWeeklyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<WeeklyTaskAppliedQuarterly> qWeeklyTasks =
                weeklyTaskSchedulingService.getAllUsersWeeklyTasksAppliedQuarterly(user.getId());
        model.addAttribute("qWeeklyTasks", qWeeklyTasks);
        model.addAttribute("user", user);
        return "tasks/quarterly-weekly-tasks-scheduled";
    }

    // In the form the user selects the weekly task scheduler along with the year and quarter
    // In the service method, the scheduling of the weekly tasks will be triggered for the quarter
    @PostMapping("/submit-quarterly-weekly-tasks-scheduled/{quarter}/{year}")
    public String saveNewQuarterlyWeeklyTask(
            @ModelAttribute("qWeeklyTask")
            WeeklyTaskAppliedQuarterly qWeeklyTask, Model model,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") Integer year) {
        try {
            qWeeklyTask.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qWeeklyTask.setYear(year);
            weeklyTaskSchedulingService.saveWeeklyTaskAppliedQuarterly(qWeeklyTask);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save weekly task scheduler, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/quarterly-weekly-tasks-scheduled";
    }
}
