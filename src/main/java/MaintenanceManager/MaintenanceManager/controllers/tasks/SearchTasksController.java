package MaintenanceManager.MaintenanceManager.controllers.tasks;

import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchMonthAndYear;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchTasksByDate;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
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
    MaintenanceTaskService maintenanceTaskService;

    @GetMapping("/search-tasks-by-date")
    public String showSearchTasksByDatePage(Model model) {
        model.addAttribute("searchTasksByDate", new SearchTasksByDate());
        return "tasks/search-tasks-by-date";
    }

    @GetMapping("/search-tasks-by-month-and-year")
    public String showSearchTasksByMonthAndYearPage(Model model) {
        Month[] monthOptions = Month.values();
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("searchMonthAndYear",
                new SearchMonthAndYear());
        return "tasks/search-tasks-by-month-and-year";
    }

    @PostMapping("/search-tasks-by-month-year")
    public String searchTasksByMonth(
            @ModelAttribute("searchMonthAndYear")
            SearchMonthAndYear searchMonthAndYear,
            Model model, Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Month month = searchMonthAndYear.getMonth();
        int queryMonth = month.getValue();
        System.out.println("This is the query month: " + queryMonth);
        int queryYear = searchMonthAndYear.getYear();
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
        return "tasks/tasks-by-month";
    }

}
