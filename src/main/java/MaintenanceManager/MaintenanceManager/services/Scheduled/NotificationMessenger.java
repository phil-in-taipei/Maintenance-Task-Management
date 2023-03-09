package MaintenanceManager.MaintenanceManager.services.Scheduled;

import MaintenanceManager.MaintenanceManager.models.email.EmailContent;
import MaintenanceManager.MaintenanceManager.services.Email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class NotificationMessenger {
    @Autowired
    EmailService emailService;
    @Scheduled(fixedRate = 120000) public void scheduleTask()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss.SSS");

        String strDate = dateFormat.format(new Date());

        System.out.println(
                "Fixed rate Scheduler: Task running at - "
                        + strDate);
        System.out.println("Attempting to send email:");

        emailService.sendDailyTaskReminderEMail(
                new EmailContent("sweeney.phil@gmx.com",
                        "Sending a test email", "Testing SMTP in Spring")
        );
    }
}
