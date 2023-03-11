package MaintenanceManager.MaintenanceManager.models.tasks;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntervalTaskGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String taskGroupName;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal taskGroupOwner;

    // orphanRemoval deletes related maintenance tasks in the group upon deletion
    @OneToMany(mappedBy = "intervalTaskGroup", orphanRemoval = true)
    private Set<MaintenanceTask> maintenanceTasks;

    // orphanRemoval deletes related interval tasks in the group upon deletion
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "interval_task_group_id")
    private List<IntervalTask> intervalTasks;

    @Column(nullable = false)
    private int intervalInDays;

    // orphanRemoval deletes related quarterly applications
    // of the IntervalTaskGroup upon deletion
    @OneToMany(mappedBy = "intervalTaskGroup", orphanRemoval = true)
    private Set<IntervalTaskGroupAppliedQuarterly> intervalTaskGroupAppliedQuarterly;

    @Override
    public String toString() {
        return "IntervalTaskGroup{" +
                "id=" + id +
                ", taskGroupName='" + taskGroupName + '\'' +
               // ", maintenanceTasks=" + maintenanceTasks +
                //", intervalTasks=" + intervalTasks +
                ", intervalInDays=" + intervalInDays +
                '}';
    }

    // this is for forms in thymeleaf to have a readable String
    public String templateSelector() {
        return taskGroupName + " " + "(Every " +
                intervalInDays + " days)";
    }
}
