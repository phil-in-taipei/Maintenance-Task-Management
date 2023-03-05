package MaintenanceManager.MaintenanceManager.models.tasks;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

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
    private DayOfWeek dayOfWeek;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

    @Override
    public String toString() {
        return "WeeklyTaskScheduler{" +
                "id=" + id +
                ", weeklyTaskName='" + weeklyTaskName + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", user=" + user +
                '}';
    }

    public String templateSelector() {
        String dayOfWeekStr = dayOfWeek
                .getDisplayName(TextStyle.FULL,
                        Locale.getDefault());
        return weeklyTaskName + " (every " + dayOfWeekStr + ")";
    }
}
