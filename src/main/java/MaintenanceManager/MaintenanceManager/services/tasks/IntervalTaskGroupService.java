package MaintenanceManager.MaintenanceManager.services.tasks;
import MaintenanceManager.MaintenanceManager.logging.Loggable;
import MaintenanceManager.MaintenanceManager.logging.MethodPerformance;
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


    // the 3 methods below delete objects with orphanRemoval option in model
    // allowing for cascading deletion of interval task group-related models
    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteIntervalTask(Long id) {
        intervalTaskRepo.deleteById(id);
    }

    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteIntervalTaskGroup(Long id) {
        intervalTaskGroupRepo.deleteById(id);
    }

    @Loggable
    @MethodPerformance
    @Transactional
    public void deleteIntervalTaskGroupAppliedQuarterly(Long id) {
        intervalTaskAppliedQuarterlyRepo.deleteById(id);
    }


    // this finds all interval task groups which have been applied quarterly
    // by the id of the interval task group (child field)
    @Loggable
    @MethodPerformance
    public List<IntervalTaskGroupAppliedQuarterly>
        getIntervalTaskGroupAppliedQuarterlyByITGId(Long id) {
         return intervalTaskAppliedQuarterlyRepo.findAllByIntervalTaskGroupId(id);
    }

    // gets all interval task groups of a given user
    @Loggable
    @MethodPerformance
    public List<IntervalTaskGroup> getAllUsersIntervalTaskGroups(String username) { // Long userId
        //return intervalTaskGroupRepo.findAllByTaskGroupOwnerId(username);
        return intervalTaskGroupRepo.findAllByTaskGroupOwnerUsername(username);

    }

    // gets all interval task groups which have NOT already been scheduled during
    // a given quarter/year -- this is for the selectors in the form template
    @Loggable
    @MethodPerformance
    public List<IntervalTaskGroup>
        getAllUsersIntervalTaskGroupsAvailableForQuarterAndYear( //Long userId
                String username, QuarterlySchedulingEnum quarter, Integer year) {
            List<IntervalTaskGroup> allUsersITGs =
                    getAllUsersIntervalTaskGroups(username); //userId
            List<IntervalTaskGroup> alreadyScheduledITGS
                    = getAllITGsAlreadyScheduledForQuarterAndYear(
                    quarter, year, username);
            for (IntervalTaskGroup aSITG : alreadyScheduledITGS) {
                allUsersITGs.remove(aSITG);
            }
            return allUsersITGs;
    }

    // gets  a record of all interval task groups which a
    // maintenance user has already applied quarterly
    @Loggable
    @MethodPerformance
    public List<IntervalTaskGroupAppliedQuarterly>
        getAllUsersIntervalTaskGroupsAppliedQuarterly(String username) { // Long userId
        return intervalTaskAppliedQuarterlyRepo
                .findAllByIntervalTaskGroup_TaskGroupOwnerUsernameOrderByYearAscQuarterAsc(
                        username);
        //return intervalTaskAppliedQuarterlyRepo
        //        .findAllByIntervalTaskGroup_TaskGroupOwnerIdOrderByYearAscQuarterAsc(
        //                userId);
    }

    // gets  a record of all interval task groups which a
    // maintenance user has already applied during a given quarter and year
    @Loggable
    @MethodPerformance
    public List<IntervalTaskGroupAppliedQuarterly>
        getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                QuarterlySchedulingEnum quarter, Integer year, String username//Long userId
        ) {
        return intervalTaskAppliedQuarterlyRepo
                .findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerUsername(
                        quarter, year, username
                );
        //return intervalTaskAppliedQuarterlyRepo
        //        .findAllByQuarterAndYearAndIntervalTaskGroup_TaskGroupOwnerId(
        //                quarter, year, userId
        //        );
    }

    // gets all interval task groups which have already been scheduled in a quarter
    // to prevent double booking/database integrity error
    @Loggable
    @MethodPerformance
    List<IntervalTaskGroup>
        getAllITGsAlreadyScheduledForQuarterAndYear(
                QuarterlySchedulingEnum quarter, Integer year, String username //Long userId
        ) {
            List<IntervalTaskGroupAppliedQuarterly> qITGAQs =
                    getUsersIntervalTaskGroupsAppliedQuarterlyByQuarterAndYear(
                            quarter, year, username); //userId
            List<IntervalTaskGroup> alreadyScheduledIntervalTaskGroups = new ArrayList<>();
            for (IntervalTaskGroupAppliedQuarterly iTGAQ : qITGAQs) {
                alreadyScheduledIntervalTaskGroups.add(iTGAQ.getIntervalTaskGroup());
            }
            return alreadyScheduledIntervalTaskGroups;
    }

    // gets the interval task (one member of a group),
    // this is to check that it exists prior to deletions
    @Loggable
    @MethodPerformance
    public IntervalTask getIntervalTask(Long id) {
        return intervalTaskRepo.findById(id)
                .orElse(null);
    }

    // gets the interval task group applied, this is
    // to check that it exists prior to deletions
    @Loggable
    @MethodPerformance
    public IntervalTaskGroup getIntervalTaskGroup(Long id) {
        return intervalTaskGroupRepo.findById(id)
                .orElse(null);
    }

    // gets the interval task group applied quarterly, this is to
    // check that it exists prior to deletions
    @Loggable
    @MethodPerformance
    public IntervalTaskGroupAppliedQuarterly
        getIntervalTaskGroupAppliedQuarterly(Long id) {
            return intervalTaskAppliedQuarterlyRepo.findById(id)
                .orElse(null);
    }

    // saves one task scheduler which will belong to the interval task groups
    // note: This appears to not be used (try deleting method in another branch)
    // using the method below allows for a verification that the interval task group exists
    // prior to submitting, so it is used instead of this method
    @Loggable
    @MethodPerformance
    @Transactional
    public void saveIntervalTask(IntervalTask intervalTask)
            throws IllegalArgumentException {
        intervalTaskRepo.save(intervalTask);
    }

    // saves group of task schedulers which are alternatively applied at specified daily intervals
    // this method is also called to save interval tasks belonging to the group
    // this allows for a verification that the interval task group exists
    @Loggable
    @MethodPerformance
    @Transactional
    public IntervalTaskGroup saveIntervalTaskGroup(IntervalTaskGroup intervalTaskGroup)
            throws IllegalArgumentException {
        return intervalTaskGroupRepo.save(intervalTaskGroup);
    }

    // saves an interval task group quarterly/yearly application and triggers the
    // member tasks to be scheduled at specified daily intervals during the quarter
    @Loggable
    @MethodPerformance
    @Transactional
    public IntervalTaskGroupAppliedQuarterly saveIntervalTaskGroupAppliedQuarterly(
            IntervalTaskGroupAppliedQuarterly intervalTaskGroupAppliedQuarterly)
            throws IllegalArgumentException {
        //Preparing to save quarterly-applied interval task groups
        IntervalTaskGroup intervalTaskGroup = intervalTaskGroupAppliedQuarterly
                .getIntervalTaskGroup();

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
                generateTaskBatchesService
                        .generateTaskBatchByDateListAndIntervalTaskList(
                    intervalTaskGroup, schedulingDates
                );

        // Saving batch of tasks
        maintenanceTaskService.saveBatchOfTasks(batchOfTasks);
        // saving quarterly-applied interval task group object
        return intervalTaskAppliedQuarterlyRepo
                .save(intervalTaskGroupAppliedQuarterly);
    }
}
