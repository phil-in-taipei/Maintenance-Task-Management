package MaintenanceManager.MaintenanceManager.services.email;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
import MaintenanceManager.MaintenanceManager.models.email.EmailContent;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    @Value("${spring.mail.username}") private String sender;

    // simple helper method to send automated email with smtp
    @Loggable
    @MethodPerformance
    public void sendEMail(EmailContent emailContent)
    {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailContent.getRecipient());
            mailMessage.setText(emailContent.getMsgBody());
            mailMessage.setSubject(emailContent.getSubject());
            javaMailSender.send(mailMessage);
        }
        catch (Exception e) {
            System.out.println("***************Error sending email: " + e.toString() + "*********************");
        }
    }

    @Loggable
    @MethodPerformance
    public void sendDailyReminderMessage(UserPrincipal user)
    {

        LocalDate today = LocalDate.now();

        // This is the result of the query for user's tasks today
        List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasksByDate(
                user.getUsername(), today); // user.getId();

        String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));

        // this will check if anything is scheduled in the query, and if so, construct a string
        // with the scheduled tasks listed and numbered for the automated email notification
        if (!maintenanceTasks.isEmpty()) {
            int itemNumber = 1;
            StringBuilder messageBody = new StringBuilder(
                    "Good morning, \n\nThe following tasks have been scheduled for " + formattedDate + ":");
            for (MaintenanceTask task : maintenanceTasks) {
                messageBody.append("\n");
                messageBody.append(Integer.toString(itemNumber) + ".) ");
                messageBody.append(task.getTaskName());
                itemNumber++;
            }
            messageBody.append("\n\nHave a nice day!");
            // The query list was not empty, so the message will be sent
            sendEMail(
                    new EmailContent(user.getUserMeta().getEmail(),
                            messageBody.toString(), "Maintenance Task Reminder: " + formattedDate)
            );
        } else {
            System.out.println("***********No tasks scheduled today!*********");
        }

    }

}
