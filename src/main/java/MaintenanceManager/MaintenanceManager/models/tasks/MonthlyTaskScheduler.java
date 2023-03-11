package MaintenanceManager.MaintenanceManager.models.tasks;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;
import java.util.ArrayList;
import java.util.Set;

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
    private Integer dayOfMonth;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

    // orphanRemoval deletes related quarterly applications
    // of the MonthlyTaskScheduler upon deletion
    @OneToMany(mappedBy = "monthlyTaskScheduler", orphanRemoval = true)
    private Set<MonthlyTaskAppliedQuarterly> monthlyTaskAppliedQuarterly;

    @Override
    public String toString() {
        return "MonthlyTaskScheduler{" +
                "id=" + id +
                ", Monthly Task Name='" + monthlyTaskName + '\'' +
                ", Day Of Month=" + dayOfMonth +
                '}';
    }

    // this is for forms in thymeleaf to have a readable String
    public String templateSelector() {
        String ordinalNumber = "th";
        if (dayOfMonth == 1 || dayOfMonth == 21 || dayOfMonth == 31) {
            ordinalNumber = "st";
        } if (dayOfMonth == 3 || dayOfMonth == 23) {
            ordinalNumber = "rd";
        }
        return monthlyTaskName + ": " + dayOfMonth + ordinalNumber +" day of month ";
    }
}
