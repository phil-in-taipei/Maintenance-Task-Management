package MaintenanceManager.MaintenanceManager.controllers.tasks;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MonthlyTaskScheduler;
import MaintenanceManager.MaintenanceManager.models.tasks.QuarterlySchedulingEnum;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.RecurringTaskAppliedQuarterlyForm;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchQuarterAndYear;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.MonthlyTaskSchedulingService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Controller
public class MonthlyTaskSchedulerController {

    @Autowired
    MonthlyTaskSchedulingService monthlyTaskSchedulingService;

    @Autowired
    UserDetailsServiceImplementation userService;

    // directs user to form to apply monthly schedulers
    // to a specific quarter/year. It will also trigger batch scheduling
    // of task on the specified day of month throughout the quarter
    @PostMapping("/apply-monthly-schedulers")
    public String showApplyMonthlySchedulerFormPage(
            @ModelAttribute("monthlyTaskQuarterAndYear")
            SearchQuarterAndYear monthlyTaskQuarterAndYear,
            Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<MonthlyTaskScheduler> availableMonthlyTasks =
                monthlyTaskSchedulingService
                        .getAllUsersMonthlyTaskSchedulersAvailableForQuarterAndYear(
                               user.getUsername(),
                                QuarterlySchedulingEnum.valueOf(
                                        monthlyTaskQuarterAndYear.getQuarter()),
                                        monthlyTaskQuarterAndYear.getYear()
                        );
        System.out.println(availableMonthlyTasks.toString());
        model.addAttribute("monthlyTasks", availableMonthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("quarter", monthlyTaskQuarterAndYear.getQuarter());
        model.addAttribute("year", monthlyTaskQuarterAndYear.getYear());
        //MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
        RecurringTaskAppliedQuarterlyForm qMonthlyTask = new RecurringTaskAppliedQuarterlyForm();
        model.addAttribute("qMonthlyTask", qMonthlyTask);
        return "tasks/apply-monthly-schedulers";
    }

    // directs user to form for the creation of a monthly scheduler
    @GetMapping("/create-monthly-task-scheduler")
    public String showCreateMonthlyTaskFormPage(Model model) {
        MonthlyTaskScheduler monthlyTaskScheduler = new MonthlyTaskScheduler();
        model.addAttribute("monthlyTaskScheduler", monthlyTaskScheduler);
        return "tasks/create-monthly-task-scheduler";
    }

    // link to delete monthly scheduler. Returns an error if the id does not match
    // a weekly scheduler (does not exist)
    @RequestMapping("/delete-monthly-task-scheduler/{id}")
    public String deleteMonthlyMaintenanceTask(
            @PathVariable(name = "id") Long id, Model model) {
        if (monthlyTaskSchedulingService.getMonthlyTaskScheduler(id) == null) {
            model.addAttribute("message",
                    "Cannot delete, monthly task with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        monthlyTaskSchedulingService.deleteMonthlyTaskScheduler(id);
        return "redirect:/monthly-tasks";
    }

    // link to delete quarterly applied record of monthly scheduler.
    // Returns an error if the id does not match an object in the database (does not exist)
    @RequestMapping("/delete-monthly-task-applied-quarterly/{id}")
    public String deleteMonthlyTaskAppliedQuarterly(
            @PathVariable(name = "id") Long id, Model model) {
        if (monthlyTaskSchedulingService
                .getMonthlyTaskAppliedQuarterly(id) == null) {
            model.addAttribute(
                    "message",
                    "Cannot delete, monthly task " +
                            "applied quarterly with id: " +
                            id + " does not exist.");
            return "error/error";
        }
        monthlyTaskSchedulingService.deleteMonthlyTaskAppliedQuarterly(id);
        return "redirect:/quarterly-monthly-tasks-scheduled";
    }

    // link for posting request to create new monthly task scheduler
    @PostMapping("/monthly-tasks")
    public String saveNewMonthlyTaskScheduler(
            @ModelAttribute("monthlyTaskScheduler")
            MonthlyTaskScheduler monthlyTaskScheduler, Model model,
            Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
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

    // shows all monthly task schedulers which have been created by the authenticated user
    @GetMapping("/monthly-tasks")
    public String showAllUserMonthlyTasks(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<MonthlyTaskScheduler> monthlyTasks =
                monthlyTaskSchedulingService
                        .getAllUsersMonthlyTaskSchedulers(userDetails.getUsername()); // user.getId()
        model.addAttribute("monthlyTasks", monthlyTasks);
        model.addAttribute("user", user);
        model.addAttribute("monthlyTaskQuarterAndYear",
                new SearchQuarterAndYear());
        return "tasks/monthly-task-schedulers";
    }

    // shows record of all quarterly/yearly application of monthly task schedulers
    // these correspond to the scheduling of the specified monthly task on the date
    // of month throughout the quarter
    @GetMapping("/quarterly-monthly-tasks-scheduled")
    public String showAllUserQuarterlyMonthlyTasks(
            Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrincipal user = userService.loadUserByUsername(userDetails.getUsername());
        List<MonthlyTaskAppliedQuarterly> qMonthlyTasks =
                monthlyTaskSchedulingService
                        .getAllUsersMonthlyTasksAppliedQuarterly(userDetails.getUsername());
        model.addAttribute("qMonthlyTasks", qMonthlyTasks);
        model.addAttribute("user", user);
        return "tasks/quarterly-monthly-tasks-scheduled";
    }

    // In the form the user selects the monthly task scheduler along with the year and quarter
    // In the service method, the scheduling of the monthly tasks will be triggered for the quarter
    @PostMapping("/submit-quarterly-monthly-tasks-scheduled/{quarter}/{year}")
    public String saveNewQuarterlyMonthlyTask(
            @ModelAttribute("qMonthlyTask")
            RecurringTaskAppliedQuarterlyForm qMonthlyTaskForm, Model model,
            @PathVariable(name = "quarter") String quarter,
            @PathVariable(name = "year") Integer year,
            Authentication authentication) {
        try {
            MonthlyTaskScheduler monthlyTask = monthlyTaskSchedulingService.getMonthlyTaskScheduler(
                    qMonthlyTaskForm.getRecurringTaskSchedulerId());
            MonthlyTaskAppliedQuarterly qMonthlyTask = new MonthlyTaskAppliedQuarterly();
            qMonthlyTask.setMonthlyTaskScheduler(monthlyTask);
            qMonthlyTask.setQuarter(QuarterlySchedulingEnum.valueOf(quarter));
            qMonthlyTask.setYear(year);
            monthlyTaskSchedulingService.saveMonthlyTaskAppliedQuarterly(qMonthlyTask);
        } catch (IllegalArgumentException e) {
            model.addAttribute(
                    "message",
                    "Could not save monthly task scheduler, "
                            + e.getMessage());
            return "error/error";
        }
        return "redirect:/quarterly-monthly-tasks-scheduled";
    }
}
