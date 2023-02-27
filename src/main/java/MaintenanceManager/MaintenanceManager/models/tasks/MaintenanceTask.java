package MaintenanceManager.MaintenanceManager.models.tasks;
import MaintenanceManager.MaintenanceManager.models.user.UserPrincipal;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatusEnum status;

    @Column(nullable = true)
    private String comments;

    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;

    @Column(nullable = false)
    private Integer timesModified;

    @Column(nullable = false)
    private Boolean noRainOnly;

    @ManyToOne(
            cascade = CascadeType.ALL,
            optional = true
    )
    private IntervalTaskGroup intervalTaskGroup;

    public MaintenanceTask(
            String taskName, String description,
            LocalDate date, UserPrincipal user) {
        this.taskName = taskName;
        this.description = description;
        this.date = date;
        this.user = user;
        this.status = TaskStatusEnum.PENDING;
        this.createdDateTime = LocalDateTime.now();
        this.updatedDateTime = LocalDateTime.now();
        this.noRainOnly = false;
        this.timesModified = 1;
    }

    public MaintenanceTask(
            String taskName, String description,
            LocalDate date, UserPrincipal user,
            Boolean noRainOnly,
            IntervalTaskGroup intervalTaskGroup) {
        this.taskName = taskName;
        this.description = description;
        this.date = date;
        this.user = user;
        this.status = TaskStatusEnum.PENDING;
        this.createdDateTime = LocalDateTime.now();
        this.updatedDateTime = LocalDateTime.now();
        this.noRainOnly = noRainOnly;
        this.timesModified = 1;
        this.intervalTaskGroup = intervalTaskGroup;
    }

    @Override
    public String toString() {
        return "MaintenanceTask{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", user=" + user.getUsername() +
                ", status=" + status +
                ", comments='" + comments + '\'' +
                ", createdDateTime=" + createdDateTime +
                ", updatedDateTime=" + updatedDateTime +
                ", timesModified=" + timesModified +
                ", noRainOnly=" + noRainOnly +
                ", intervalTaskGroup=" + intervalTaskGroup +
                '}';
    }
}
