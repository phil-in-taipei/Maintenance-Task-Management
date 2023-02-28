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
        List<IntervalTask> intervalTasks = intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup().getIntervalTasks();
        int lengthOfIntervalTasks = intervalTasks.size();
        int lastIndexInIntervalTaskList = lengthOfIntervalTasks - 1;
        System.out.println("*****************Interval Tasks/length " +  lengthOfIntervalTasks + " ********************");
        System.out.println(intervalTasks.toString());
        List<LocalDate> schedulingDates =
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                       intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup().getIntervalInDays(),
                        intervalTaskGroupAppliedQuarterly.getYear(), intervalTaskGroupAppliedQuarterly.getQuarter()
                );
        int lengthOfDates = schedulingDates.size();
        System.out.println("*****************Dates/length " +  lengthOfDates +" *****************************");
        System.out.println(schedulingDates.toString());


        List<MaintenanceTask> tasks = new ArrayList<>();
        System.out.println("****************Now iterating through dates/tasks and matching them***********");
        System.out.println("****************While adding the tasks to a List***********");
        int indexOfIntervalTaskList = 0;
        for (LocalDate date : schedulingDates) {
            System.out.println(date + ": " + intervalTasks.get(indexOfIntervalTaskList).toString());
            IntervalTask intervalTask = intervalTasks.get(indexOfIntervalTaskList);
            IntervalTaskGroup intervalTaskGroup = intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup();
            MaintenanceTask maintenanceTask = new MaintenanceTask(
                    intervalTask.getIntervalTaskName(), intervalTask.getDescription(),
                    date, intervalTaskGroup.getTaskGroupOwner(), intervalTask.getNoRainOnly(),
                    intervalTaskGroup
            );
            System.out.println("*****************Maintenance task to be added*******************");
            System.out.println(maintenanceTask.toString());
            //maintenanceTaskService.saveTask(maintenanceTask);
            tasks.add(maintenanceTask);
            if (indexOfIntervalTaskList == lastIndexInIntervalTaskList) {
                indexOfIntervalTaskList = 0;
            } else {
                indexOfIntervalTaskList++;
            }
        }
        System.out.println("*****************Now saving batch of tasks*******************");
        maintenanceTaskService.saveBatchOfTasks(tasks);
        System.out.println("*****************Now saving qITG object*******************");
        intervalTaskAppliedQuarterlyRepo.save(intervalTaskGroupAppliedQuarterly);
    }
}
