package MaintenanceManager.MaintenanceManager.models.tasks;
import java.time.DayOfWeek;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTaskScheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String weeklyTaskName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

    @Override
    public String toString() {
        return "WeeklyTaskScheduler{" +
                "id=" + id +
                ", weeklyTaskName='" + weeklyTaskName + '\'' +
                ", description='" + description + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", user=" + user +
                '}';
    }
}
