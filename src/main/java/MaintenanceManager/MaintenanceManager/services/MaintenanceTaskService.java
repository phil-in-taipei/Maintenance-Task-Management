package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.repositories.MaintenanceTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceTaskService {
    @Autowired
    MaintenanceTaskRepo maintenanceTaskRepo;

    public List<MaintenanceTask> getAllUserTasks(Long userId) {
        return maintenanceTaskRepo.findAllByUserId(userId);
    }

    @Transactional
    public MaintenanceTask saveTask(MaintenanceTask task)
            throws IllegalArgumentException {
        return maintenanceTaskRepo.save(task);
    }
}
