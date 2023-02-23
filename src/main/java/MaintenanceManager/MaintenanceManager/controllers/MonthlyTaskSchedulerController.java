package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.MonthlyTaskQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.MonthlyTaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Controller
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;


    @PostMapping("/apply-monthly-schedulers")
    public String searchTasksByDate(
            @ModelAttribute("monthlyTaskQuarterAndYear")
            MonthlyTaskQuarterAndYear monthlyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the quarter: " + monthlyTaskQuarterAndYear.getQuarter());
        System.out.println("This is the year: " + monthlyTaskQuarterAndYear.getYear());
        List<MonthlyTaskScheduler> monthlyTasks =
                monthlyTaskSchedulingService.getAllUsersMonthlyTaskSchedulers(user.getId());
        System.out.println(monthlyTasks.toString());
        model.addAttribute("monthlyTasks", monthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("quarter", monthlyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", monthlyTaskQuarterAndYear.getYear());
        MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
        //qMonthlyTask.setYear(monthlyTaskQuarterAndYear.getYear());
        //qMonthlyTask.setQuarter(QuarterlySchedulingEnum.valueOf(
        //        monthlyTaskQuarterAndYear.getQuarter()));
        return "apply-monthly-schedulers";
    }

    @GetMapping("/create-monthly-task-scheduler")
    public String showSubmitTaskFormPage(Model model) {
        MonthlyTaskScheduler monthlyTaskScheduler = new MonthlyTaskScheduler();
        model.addAttribute("monthlyTaskScheduler", monthlyTaskScheduler);
        return "create-monthly-task-scheduler";
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
            return "error";
        }
        return "redirect:/monthly-tasks";
    }

    @GetMapping("/monthly-tasks")
    public String showAllUserTasks(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<MonthlyTaskScheduler> monthlyTasks =
                monthlyTaskSchedulingService.getAllUsersMonthlyTaskSchedulers(user.getId());
        model.addAttribute("monthlyTasks", monthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("monthlyTaskQuarterAndYear", new MonthlyTaskQuarterAndYear());
        return "monthly-task-schedulers";
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
        return "quarterly-monthly-tasks-scheduled";
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
            return "error";
        }
        return "redirect:/quarterly-monthly-tasks-scheduled";
    }
}
