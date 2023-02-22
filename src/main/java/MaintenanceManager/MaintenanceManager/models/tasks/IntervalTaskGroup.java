package MaintenanceManager.MaintenanceManager.models.tasks;
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

    @OneToMany(mappedBy = "intervalTaskGroup")
    private Set<MaintenanceTask> maintenanceTasks;

    @Column(nullable = false)
    private int intervalInDays;
}
