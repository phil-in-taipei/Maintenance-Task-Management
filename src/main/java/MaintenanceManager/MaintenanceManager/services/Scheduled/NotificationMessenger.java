package MaintenanceManager.MaintenanceManager.services.Scheduled;

import MaintenanceManager.MaintenanceManager.models.email.EmailContent;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.Email.EmailService;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;

@Component
public class NotificationMessenger {
    @Autowired
    EmailService emailService;

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    @Scheduled(fixedRate = 120000) public void sendDailyMaintenanceTasksReminder() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss.SSS");

        String strDate = dateFormat.format(new Date());

        System.out.println(
                "Fixed rate Scheduler: Task running at - "
                        + strDate);
        System.out.println("Attempting to send emails:");
        List<UserPrincipal> maintenanceUsers = userDetailsService.getAllMaintenanceUsers();
        for (UserPrincipal user : maintenanceUsers) {
            System.out.println("Sending email for " + user.getUsername());
            emailService.sendDailyReminderMessage(user);
        }

    }
}
