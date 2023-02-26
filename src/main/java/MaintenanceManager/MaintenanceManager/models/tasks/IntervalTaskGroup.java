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

    // (probably can delete field below)
    @OneToMany(mappedBy = "intervalTaskGroup")
    private Set<MaintenanceTask> maintenanceTasks;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "interval_task_group_id")
    private List<IntervalTask> intervalTasks;

    @Column(nullable = false)
    private int intervalInDays;

    @Override
    public String toString() {
        return "IntervalTaskGroup{" +
                "id=" + id +
                ", taskGroupName='" + taskGroupName + '\'' +
                ", maintenanceTasks=" + maintenanceTasks +
                ", intervalTasks=" + intervalTasks +
                ", intervalInDays=" + intervalInDays +
                '}';
    }
}
