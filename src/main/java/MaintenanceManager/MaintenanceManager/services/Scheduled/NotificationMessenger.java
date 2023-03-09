package MaintenanceManager.MaintenanceManager.services.Scheduled;

import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.Email.EmailService;
import MaintenanceManager.MaintenanceManager.services.users.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMessenger {
    @Autowired
    EmailService emailService;

    @Autowired
    UserDetailsServiceImplementation userDetailsService;

    // this is scheduled to occur at 5:00 am each day in the Taipei time zone
    @Loggable
    // every two minutes when testing it out
    //@Scheduled(fixedRate = 120000000) public void sendDailyMaintenanceTasksReminder() {
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Taipei") public void sendDailyMaintenanceTasksReminder() {
        // this will send a notification email to each of the users who have
        // ROLE_MAINTENANCE authority
        // with a reminder of the tasks which are scheduled on that day
        System.out.println("Sending message now!!!");
        List<UserPrincipal> maintenanceUsers = userDetailsService
                .getAllMaintenanceUsers();
        for (UserPrincipal user : maintenanceUsers) {
            emailService.sendDailyReminderMessage(user);
        }

    }
}
