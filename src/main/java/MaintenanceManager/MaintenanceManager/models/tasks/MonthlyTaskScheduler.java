package MaintenanceManager.MaintenanceManager.models.tasks;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTaskScheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String monthlyTaskName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer dayOfMonth;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;
}
