package MaintenanceManager.MaintenanceManager.models.tasks;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "intervalTaskGroup")
    private Set<MaintenanceTask> maintenanceTasks;

    @Column(nullable = false)
    private int intervalInDays;
}
