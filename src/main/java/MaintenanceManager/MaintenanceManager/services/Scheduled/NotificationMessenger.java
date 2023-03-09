package MaintenanceManager.MaintenanceManager.services.Scheduled;

import MaintenanceManager.MaintenanceManager.models.email.EmailContent;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.services.Email.EmailService;
import MaintenanceManager.MaintenanceManager.services.tasks.MaintenanceTaskService;
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
    MaintenanceTaskService maintenanceTaskService;
    @Scheduled(fixedRate = 120000) public void scheduleTask() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss.SSS");

        String strDate = dateFormat.format(new Date());

        System.out.println(
                "Fixed rate Scheduler: Task running at - "
                        + strDate);
        System.out.println("Attempting to send email:");
        LocalDate today = LocalDate.now();
        List<MaintenanceTask> maintenanceTasks = maintenanceTaskService.getAllUserTasksByDate(
                16L, today);

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

        emailService.sendDailyTaskReminderEMail(
                new EmailContent("sweeney.phil@gmx.com",
                        messageBody.toString(), "Task Reminder: " + formattedDate)
        );
    }
}
