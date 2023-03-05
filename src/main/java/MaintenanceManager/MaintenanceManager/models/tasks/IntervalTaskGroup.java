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

    // for query about the weather condition and rescheduling
    // by switching with a possible later one in the same
    // interval task group (probably switch to just one way through maintenance task)
    @OneToMany(mappedBy = "intervalTaskGroup", orphanRemoval = true)
    private Set<MaintenanceTask> maintenanceTasks;


    // apply this configuration to the "applied quarterlies"
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "interval_task_group_id")
    private List<IntervalTask> intervalTasks;

    @Column(nullable = false)
    private int intervalInDays;

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

    public String templateSelector() {
        return taskGroupName + " " + "(Every " +
                intervalInDays + " days)";
    }
}
