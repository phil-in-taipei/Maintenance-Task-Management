package MaintenanceManager.MaintenanceManager.services.Email;
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
            System.out.println("Error sending email: " + e.toString());
        }
    }

    public void sendDailyReminderMessage(UserPrincipal user)
    {

        LocalDate today = LocalDate.now();
        List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasksByDate(
                user.getId(), today);

        String formattedDate = today.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));

        System.out.println("**************Result of query for user's tasks today***********");
        System.out.println(maintenanceTasks.toString());
        StringBuilder messageBody = null;
        if (!maintenanceTasks.isEmpty()) {
            int itemNumber = 1;
            messageBody = new StringBuilder(
                    "Good morning, \n\nThe following tasks have been scheduled for " + formattedDate + ":");
            for (MaintenanceTask task : maintenanceTasks) {
                messageBody.append("\n");
                messageBody.append(Integer.toString(itemNumber) + ".) ");
                messageBody.append(task.getTaskName());
                itemNumber++;
            }
            messageBody.append("\n\nHave a nice day!");
            System.out.println("***********Not empty -- this is the message that will be sent: ************");
            System.out.println(messageBody);
        } else {
            System.out.println("***********No tasks scheduled today!*********");
        }

        sendEMail(
                new EmailContent(user.getUserMeta().getEmail(),
                        messageBody.toString(), "Maintenance Task Reminder: " + formattedDate)
        );
    }

}
