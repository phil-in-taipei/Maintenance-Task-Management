package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.models.tasks.*;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskAppliedQuarterlyRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskGroupRepo;
import MaintenanceManager.MaintenanceManager.repositories.tasks.IntervalTaskRepo;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateDatesService;
import MaintenanceManager.MaintenanceManager.services.utiltities.GenerateTaskBatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Loggable
    @Transactional
    public void deleteIntervalTask(Long id) {
        intervalTaskRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteIntervalTaskGroup(Long id) {
        intervalTaskGroupRepo.deleteById(id);
    }

    @Loggable
    @Transactional
    public void deleteIntervalTaskGroupAppliedQuarterly(Long id) {
        intervalTaskAppliedQuarterlyRepo.deleteById(id);
    }

    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
        getIntervalTaskGroupAppliedQuarterlyByITGId(Long id) {
         return intervalTaskAppliedQuarterlyRepo.findAllByIntervalTaskGroupId(id);
    }

    @Loggable
    public List<IntervalTaskGroup> getAllUsersIntervalTaskGroups(Long userId) {
        return intervalTaskGroupRepo.findAllByTaskGroupOwnerId(userId);
    }

    @Loggable
    public List<IntervalTaskGroup>
        getAllUsersIntervalTaskGroupsAvailableForQuarterAndYear(
                Long userId, QuarterlySchedulingEnum quarter, Integer year) {
            List<IntervalTaskGroup> allUsersITGs =
                    getAllUsersIntervalTaskGroups(userId);
            List<IntervalTaskGroup> alreadyScheduledITGS
                    = getAllITGsAlreadyScheduledForQuarterAndYear(
                    quarter, year, userId);
            for (IntervalTaskGroup aSITG : alreadyScheduledITGS) {
                allUsersITGs.remove(aSITG);
            }
            return allUsersITGs;
    }

    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
        getAllUsersIntervalTaskGroupsAppliedQuarterly(Long userId) {
        return intervalTaskAppliedQuarterlyRepo
                .findAllByIntervalTaskGroup_TaskGroupOwnerIdOrderByYearAscQuarterAsc(
                        userId);
    }

    @Loggable
    public List<IntervalTaskGroupAppliedQuarterly>
        getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                QuarterlySchedulingEnum quarter, Integer year, Long userId
        ) {
        return intervalTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerId(
                        quarter, year, userId
                );
    }

    @Loggable
    List<IntervalTaskGroup>
        getAllITGsAlreadyScheduledForQuarterAndYear(
                QuarterlySchedulingEnum quarter, Integer year, Long userId
        ) {
            List<IntervalTaskGroupAppliedQuarterly> qITGAQs =
                    getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                            quarter, year, userId);
            List<IntervalTaskGroup> alreadyScheduledIntervalTaskGroups = new ArrayList<>();
            for (IntervalTaskGroupAppliedQuarterly iTGAQ : qITGAQs) {
                alreadyScheduledIntervalTaskGroups.add(iTGAQ.getIntervalTaskGroup());
            }
            return alreadyScheduledIntervalTaskGroups;
    }

    @Loggable
    public IntervalTask getIntervalTask(Long id) {
        return intervalTaskRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    public IntervalTaskGroup getIntervalTaskGroup(Long id) {
        return intervalTaskGroupRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    public IntervalTaskGroupAppliedQuarterly
        getIntervalTaskGroupAppliedQuarterly(Long id) {
            return intervalTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    @Loggable
    @Transactional
    public void saveIntervalTask(IntervalTask intervalTask)
            throws IllegalArgumentException {
        intervalTaskRepo.save(intervalTask);
    }

    @Loggable
    @Transactional
    public void saveIntervalTaskGroup(IntervalTaskGroup intervalTaskGroup)
            throws IllegalArgumentException {
        intervalTaskGroupRepo.save(intervalTaskGroup);
    }

    @Loggable
    @Transactional
    public void saveIntervalTaskGroupAppliedQuarterly(
            IntervalTaskGroupAppliedQuarterly intervalTaskGroupAppliedQuarterly)
            throws IllegalArgumentException {
        //Preparing to save quarterly-applied interval task groups
        IntervalTaskGroup intervalTaskGroup = intervalTaskGroupAppliedQuarterly.getIntervalTaskGroup();

        // tasks will alternatively be saved every n-th interval day
        // throughout the quarter/year specified in the submitted object
        List<LocalDate> schedulingDates =
                generateDatesService.getIntervalSchedulingDatesByQuarter(
                        intervalTaskGroup.getIntervalInDays(),
                        intervalTaskGroupAppliedQuarterly.getYear(),
                        intervalTaskGroupAppliedQuarterly.getQuarter()
                );
        // generating tasks to be scheduled on the dates in scheduling dates list
        List<MaintenanceTask> batchOfTasks =
                generateTaskBatchesService.generateTaskBatchByDateListAndIntervalTaskList(
                    intervalTaskGroup, schedulingDates
                );

        // Saving batch of tasks
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        // saving quarterly-applied interval task group object
        intervalTaskAppliedQuarterlyRepo.save(intervalTaskGroupAppliedQuarterly);
    }
}
