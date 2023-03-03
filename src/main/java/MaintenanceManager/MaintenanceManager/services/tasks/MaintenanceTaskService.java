package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.models.tasks.TaskStatusEnum;
import MaintenanceManager.MaintenanceManager.repositories.tasks.MaintenanceTaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class MaintenanceTaskService {
    @Autowired
    MaintenanceTaskRepo maintenanceTaskRepo;

    @Loggable
    public List<MaintenanceTask> getAllUserTasks(Long userId) {

        return maintenanceTaskRepo.findAllByUserIdOrderByDateAsc(userId);
    }

    @Loggable
    public List<MaintenanceTask> getAllUserTasksInDateRange(
            Long userId, LocalDate firstDate, LocalDate lastDate) {

        return maintenanceTaskRepo
                .findAllByUserIdAndDateBetweenOrderByDateAsc(userId, firstDate, lastDate);
    }

    @Loggable
    public List<MaintenanceTask> getAllUserTasksByDate(
            Long userId, LocalDate date) {

        return maintenanceTaskRepo.findAllByUserIdAndDate(userId, date);
    }

    @Loggable
    public List<MaintenanceTask> getAllUserTasksByIntervalTaskGroup(
            Long userId, Long iTgId) {
        return maintenanceTaskRepo
                .findAllByUserIdAndIntervalTaskGroupId(userId, iTgId);
    }

    public List<MaintenanceTask> getAllUncompletedPastUserTasks(Long userId) {
        return maintenanceTaskRepo.findByStatusIsNotAndDateBeforeAndUserId(
                TaskStatusEnum.COMPLETED, LocalDate.now(), userId);
    }

    @Loggable
    @Transactional
    public void saveBatchOfTasks(List<MaintenanceTask> tasks)
            throws IllegalArgumentException {
        maintenanceTaskRepo.saveAll(tasks);
    }


    @Loggable
    @Transactional
    public void saveTask(MaintenanceTask task)
            throws IllegalArgumentException {
        maintenanceTaskRepo.save(task);
    }

    @Loggable
    @Transactional
    public void confirmTaskCompletion(MaintenanceTask task)
            throws IllegalArgumentException {
                 task.setStatus(TaskStatusEnum.COMPLETED);
        maintenanceTaskRepo.save(task);
    }

    @Loggable
    public MaintenanceTask getMaintenanceTask(Long id) {
        return maintenanceTaskRepo.findById(id)
                .orElse(null);
    }
}
