package MaintenanceManager.MaintenanceManager.controllers;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.forms.SearchTasksByDate;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.models.weather.DailyForecast;
import MaintenanceManager.MaintenanceManager.services.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.WeatherApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @Autowired
    WeatherApiService weatherApiService;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/landing")
    public String landingPage(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the user: " + user);
        List<MaintenanceTask> uncompletedTasks = maintenanceTaskService.getAllUncompletedPastUserTasks(user.getId());
        List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasksByDate(
                user.getId(), LocalDate.now());
        System.out.println("These are the user's tasks: " + maintenanceTasks.toString());
        SearchTasksByDate searchTasksByDate = new SearchTasksByDate();
        Integer chanceOfRain = weatherApiService.getRainProbability(LocalDate.now().toString());
        System.out.println("*************This is the chance of rain in landing: " + chanceOfRain + "%************");
        model.addAttribute("searchTasksByDate", searchTasksByDate);
        model.addAttribute("uncompletedTasks", uncompletedTasks);
        model.addAttribute("dailyTasks", maintenanceTasks);
        model.addAttribute("user", user);
        model.addAttribute("chanceOfRain", chanceOfRain);
        return "landing";
    }

    @GetMapping("/forecast")
    public String testApiForecast(Authentication authentication, Model model) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        System.out.println("This is the user: " + user);
        List<DailyForecast> weather = weatherApiService.getDailyWeatherForecastData(LocalDate.now().toString());
        model.addAttribute("user", user);
        //model.addAttribute("weather", weather.getDailyForecasts());
        model.addAttribute("weather", weather);
        return "test-forecast";
    }
}
