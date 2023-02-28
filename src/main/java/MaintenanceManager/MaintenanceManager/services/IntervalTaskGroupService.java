package MaintenanceManager.MaintenanceManager.services;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTask;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroup;
import MaintenanceManager.MaintenanceManager.models.tasks.IntervalTaskGroupAppliedQuarterly;
import MaintenanceManager.MaintenanceManager.models.tasks.MaintenanceTask;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskGroupRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntervalTaskGroupService {

    @Autowired
    IntervalTaskRepo intervalTaskRepo;

    @Autowired
    IntervalTaskGroupRepo intervalTaskGroupRepo;

    @Autowired
    IntervalTaskAppliedQuarterlyRepo intervalTaskAppliedQuarterlyRepo;

    @Autowired
    GenerateDatesService generateDatesService;

    @Autowired
    GenerateTaskBatchesService generateTaskBatchesService;

    @Autowired
    MaintenanceTaskService maintenanceTaskService;

    public List<IntervalTaskGroup> getAllUsersIntervalTaskGroups(Long userId) {
        return intervalTaskGroupRepo.findAllByTaskGroupOwnerId(userId);
    }

    public List<IntervalTaskGroupAppliedQuarterly>
        getAllUsersIntervalTaskGroupsAppliedQuarterly(Long userId) {
        return intervalTaskAppliedQuarterlyRepo.findAllByIntervalTaskGroup_TaskGroupOwnerId(userId);
    }

    public IntervalTask getIntervalTask(Long id) {
        return intervalTaskRepo.findById(id)
                .orElse(null);
    }

    public IntervalTaskGroup getIntervalTaskGroup(Long id) {
        return intervalTaskGroupRepo.findById(id)
                .orElse(null);
    }

    @Transactional
    public void saveIntervalTask(IntervalTask intervalTask) throws IllegalArgumentException {
        intervalTaskRepo.save(intervalTask);
    }

    @Transactional
    public void deleteIntervalTask(Long id) {
        intervalTaskRepo.deleteById(id);
    }

    @Transactional
    public void saveIntervalTaskGroup(IntervalTaskGroup intervalTaskGroup)
            throws IllegalArgumentException {
        intervalTaskGroupRepo.save(intervalTaskGroup);
    }

    @Transactional
    public void saveIntervalTaskGroupAppliedQuarterly(
            IntervalTaskGroupAppliedQuarterly intervalTaskGroupAppliedQuarterly)
            throws IllegalArgumentException {
        System.out.println("*****************Now preparing to save qITG in service*****************************");
        System.out.println(intervalTaskGroupAppliedQuarterly.toString());
        System.out.println(intervalTaskGroupAppliedQuarterly.getQuarter());
        System.out.println(intervalTaskGroupAppliedQuarterly.getYear());

        List<LocalDate> schedulingDates =
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                       intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup().getIntervalInDays(),
                        intervalTaskGroupAppliedQuarterly.getYear(), intervalTaskGroupAppliedQuarterly.getQuarter()
                );
        IntervalTaskGroup intervalTaskGroup = intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup();
        List<MaintenanceTask> batchOfTasks =
                generateTaskBatchesService.generateTaskBatchByDateListAndIntervalTaskList(
                    intervalTaskGroup, schedulingDates
                );

        System.out.println("*****************Now saving batch of tasks*******************");
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        System.out.println("*****************Now saving qITG object*******************");
        intervalTaskAppliedQuarterlyRepo.save(intervalTaskGroupAppliedQuarterly);
    }
}
