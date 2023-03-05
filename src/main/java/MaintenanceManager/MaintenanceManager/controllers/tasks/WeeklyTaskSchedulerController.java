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

    @GetMapping("/create-weekly-task-scheduler")
    public String showCreateWeeklyTaskFormPage(Model model) {
        WeeklyTaskScheduler weeklyTaskScheduler = new WeeklyTaskScheduler();
        DayOfWeek[] daysOfWeekOptions = DayOfWeek.values();
        model.addAttribute("daysOfWeekOptions", daysOfWeekOptions);
        model.addAttribute("weeklyTaskScheduler", weeklyTaskScheduler);
        return "tasks/create-weekly-task-scheduler";
    }

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

    @GetMapping("/quarterly-weekly-tasks-scheduled")
    public String showAllUserQuarterlyWeeklyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<WeeklyTaskAppliedQuarterly> qWeeklyTasks =
                weeklyTaskSchedulingService.getAllUsersWeeklyTasksAppliedQuarterly(user.getId());
        System.out.println(
                "**********************************These are the qWeekly tasks for the user: " +
                        qWeeklyTasks.toString() + "***********************************");
        model.addAttribute("qWeeklyTasks", qWeeklyTasks);
        model.addAttribute("user", user);
        return "tasks/quarterly-weekly-tasks-scheduled";
    }

    @PostMapping("/submit-quarterly-weekly-tasks-scheduled/{quarter}/{year}")
    public String saveNewQuarterlyWeeklyTask(
            @ModelAttribute("qWeeklyTask")
            WeeklyTaskAppliedQuarterly qWeeklyTask, Model model,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") Integer year,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
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
