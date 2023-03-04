package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.MonthlyTaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;


    @PostMapping("/apply-monthly-schedulers")
    public String showApplyMonthlySchedulerFormPage(
            @ModelAttribute("monthlyTaskQuarterAndYear")
            SearchQuarterAndYear monthlyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the quarter: " + QuarterlySchedulingEnum.valueOf(
                monthlyTaskQuarterAndYear.getQuarter()));
        System.out.println("This is the year: " +
                monthlyTaskQuarterAndYear.getYear());

        List<MonthlyTaskScheduler> availableMonthlyTasks =
                monthlyTaskSchedulingService
                        .getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
                                user.getId(), QuarterlySchedulingEnum.valueOf(
                                        monthlyTaskQuarterAndYear.getQuarter()),
                                        monthlyTaskQuarterAndYear.getYear()
                        );
        System.out.println(availableMonthlyTasks.toString());
        model.addAttribute("monthlyTasks", availableMonthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("quarter", monthlyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", monthlyTaskQuarterAndYear.getYear());
        MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
        model.addAttribute("qMonthlyTask", qMonthlyTask);
        return "tasks/apply-monthly-schedulers";
    }

    @GetMapping("/create-monthly-task-scheduler")
    public String showCreateMonthlyTaskFormPage(Model model) {
        MonthlyTaskScheduler monthlyTaskScheduler = new MonthlyTaskScheduler();
        model.addAttribute("monthlyTaskScheduler", monthlyTaskScheduler);
        return "tasks/create-monthly-task-scheduler";
    }

    @RequestMapping("/delete-monthly-task-scheduler/{id}")
    public String deleteMonthlyMaintenanceTask(@PathVariable(name = "id") Long id, Model model) {
        if (monthlyTaskSchedulingService.getMonthlyTaskScheduler(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, monthly task with id: " + id + " does not exist.");
            return "error/error";
        }
        monthlyTaskSchedulingService.deleteMonthlyTaskScheduler(id);
        return "redirect:/monthly-tasks";
    }

    @RequestMapping("/delete-monthly-task-applied-quarterly/{id}")
    public String deleteMonthlyTaskAppliedQuarterly(@PathVariable(name = "id") Long id, Model model) {
        if (monthlyTaskSchedulingService.getMonthlyTaskAppliedQuarterly(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, monthly task applied quarterly with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        monthlyTaskSchedulingService.deleteMonthlyTaskAppliedQuarterly(id);
        return "redirect:/quarterly-monthly-tasks-scheduled";
    }

    @PostMapping("/monthly-tasks")
    public String saveNewMonthlyTaskScheduler(
            @ModelAttribute("monthlyTaskScheduler")
            MonthlyTaskScheduler monthlyTaskScheduler, Model model,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            monthlyTaskScheduler.setUser(user);
            monthlyTaskSchedulingService.saveMonthlyTaskScheduler(monthlyTaskScheduler);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save monthly task scheduler, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/monthly-tasks";
    }

    @GetMapping("/monthly-tasks")
    public String showAllUserMonthlyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MonthlyTaskScheduler> monthlyTasks =
                monthlyTaskSchedulingService.getAllUsersMonthlyTaskSchedulers(user.getId());
        model.addAttribute("monthlyTasks", monthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("monthlyTaskQuarterAndYear", new SearchQuarterAndYear());
        return "tasks/monthly-task-schedulers";
    }

    @GetMapping("/quarterly-monthly-tasks-scheduled")
    public String showAllUserQuarterlyMonthlyTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MonthlyTaskAppliedQuarterly> qMonthlyTasks =
                monthlyTaskSchedulingService.getAllUsersMonthlyTasksAppliedQuarterly(user.getId());
        System.out.println(
                "**********************************These are the qMonthly tasks for the user: " +
                        qMonthlyTasks.toString() + "***********************************");
        model.addAttribute("qMonthlyTasks", qMonthlyTasks);
        model.addAttribute("user", user);
        return "tasks/quarterly-monthly-tasks-scheduled";
    }

    @PostMapping("/submit-quarterly-monthly-tasks-scheduled/{quarter}/{year}")
    public String saveNewQuarterlyMonthlyTask(
            @ModelAttribute("qMonthlyTask")
            MonthlyTaskAppliedQuarterly qMonthlyTask, Model model,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") Integer year,
            Authentication authentication) {
        try {
            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            qMonthlyTask.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qMonthlyTask.setYear(year);
            monthlyTaskSchedulingService.saveMonthlyTaskAppliedQuarterly(qMonthlyTask);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save monthly task scheduler, "
                            + e.getMessage());
            return "tasks/error";
        }
        return "redirect:/quarterly-monthly-tasks-scheduled";
    }
}
