package MaintenanceManager.MaintenanceManager.models;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String taskName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn
    private UserPrincipal user;

    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    private TaskStatusHistory taskStatusHistory;

    public MaintenanceTask(
            String taskName, String description,
            LocalDate date, UserPrincipal user) {
        this.taskName = taskName;
        this.description = description;
        this.date = date;
        this.user = user;
        this.taskStatusHistory = new TaskStatusHistory(
                TaskStatusEnum.PENDING, LocalDateTime.now(),  LocalDateTime.now()
        );
    }

}
