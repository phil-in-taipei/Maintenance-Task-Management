package MaintenanceManager.MaintenanceManager.controllerEndpoints.tasks;

import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchMonthAndYear;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchTasksByDate;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchTasksByMonthAndYearAndUser;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Controller
public class SearchTasksController {

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    // this renders a page with a form that allows users to select a date
    // and find the user's tasks scheduled on the specified date
    @GetMapping("/search-tasks-by-date")
    public String showSearchTasksByDatePage(Model model) {
        model.addAttribute("searchTasksByDate", new SearchTasksByDate());
        return "tasks/search-tasks-by-date";
    }

    // link for search by date to be submitted. It then renders a page with the tasks
    // scheduled on the given date (if any) and the date of the previous and next day
    // for use in the template so that users can toggle to the next/previous day
    @PostMapping("/search-tasks-by-date")
    public String searchTasksByDate(
            @ModelAttribute("rescheduledTask")
            SearchTasksByDate searchTasksByDate,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        LocalDate queryDate = LocalDate.parse(searchTasksByDate.getDate());

        List<MaintenanceTask> tasks = maintenanceTaskService
                .getAllUserTasksByDate(user.getId(), queryDate);

        model.addAttribute("tasks", tasks);
        LocalDate dayBefore = queryDate.minusDays(1);
        LocalDate dayAfter = queryDate.plusDays(1);
        model.addAttribute("dayAfter", dayAfter.toString());
        model.addAttribute("dayBefore", dayBefore.toString());
        model.addAttribute("date", queryDate.toString()); //searchTasksByDate.getDate()
        model.addAttribute("user", user);
        return "tasks/tasks-by-date";
    }

    // this renders a page with a form that allows users to select a month
    // and year and find the user's tasks scheduled during the specified month
    @GetMapping("/search-tasks-by-month-and-year")
    public String showSearchTasksByMonthAndYearPage(Model model) {
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYear());
        return "tasks/search-tasks-by-month-and-year";
    }

    // link for search by month/year to be submitted. It then renders a page with the tasks
    // scheduled during the given month/year (if any)
    @PostMapping("/search-tasks-by-month-year")
    public String searchTasksByMonthAndYear(
            @ModelAttribute("searchMonthAndYear")
            SearchMonthAndYear searchMonthAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Month month = searchMonthAndYear.getMonth();
        int queryMonth = month.getValue();
        int queryYear = searchMonthAndYear.getYear();
        LocalDate date = LocalDate.now();
        // finds local date for the first day of the specified month/year
        LocalDate monthBegin = date.withDayOfMonth(1).withMonth(queryMonth)
                .withYear(queryYear);
        // finds local date for the last day of the specified month/year
        LocalDate monthEnd = monthBegin.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        Month[] monthOptions = Month.values();
        // the first and last days of the month are query arguments in the service
        // to provide all tasks scheduled by the user in the date range
        List<MaintenanceTask> tasks = maintenanceTaskService
                .getAllUserTasksInDateRange(
                        user.getId(), monthBegin, monthEnd);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYear());
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("year", queryYear);
        model.addAttribute("month", month);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks/tasks-by-month";
    }

    // this link is only available to users with admin permissions
    // this renders a page with a form that allows admin users to select a user, month
    // and year and find the user's tasks scheduled during the specified month
    @GetMapping("/search-tasks-by-month-and-year-and-user")
    public String showSearchTasksByMonthAndYearAndUserPage(Model model) {
        List<UserPrincipal> maintenanceUsers = userDetailsService.getAllMaintenanceUsers();
        model.addAttribute("maintenanceUsers", maintenanceUsers);
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchMonthAndYearAndUser",
                new SearchTasksByMonthAndYearAndUser());
        return "tasks/search-tasks-by-month-and-year-and-user";
    }

    // this link is only available to users with admin permissions
    // link for search by user/month/year to be submitted. It then renders a
    // page with the tasks scheduled for the given user during the given month/year (if any)
    @PostMapping("/search-tasks-by-month-and-year-and-user")
    public String searchTasksByMonthAndYearAndUser(
            @ModelAttribute("searchMonthAndYearAndUser")
            SearchTasksByMonthAndYearAndUser searchMonthAndYearAndUser,
            Model model, Authentication authentication) {
        UserPrincipal user = userDetailsService.getUserById(
                searchMonthAndYearAndUser.getUserId());
        Month month = searchMonthAndYearAndUser.getMonth();
        int queryMonth = month.getValue();
        System.out.println("This is the query month: " + queryMonth);
        int queryYear = searchMonthAndYearAndUser.getYear();
        LocalDate date = LocalDate.now();
        LocalDate monthBegin = date.withDayOfMonth(1).withMonth(queryMonth)
                .withYear(queryYear);
        LocalDate monthEnd = monthBegin.plusMonths(1)
                .withDayOfMonth(1).minusDays(1);
        Month[] monthOptions = Month.values();
        List<MaintenanceTask> tasks = maintenanceTaskService
                .getAllUserTasksInDateRange(
                        user.getId(), monthBegin, monthEnd);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYear());
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("year", queryYear);
        model.addAttribute("month", month);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "tasks/user-tasks-by-month";
    }

}
