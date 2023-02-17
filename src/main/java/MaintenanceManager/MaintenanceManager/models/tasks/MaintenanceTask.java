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

    //@OneToOne(cascade = CascadeType.PERSIST, optional = false)
    //private TaskStatusHistory taskStatusHistory;
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
        this.timesModified = 1;
        //this.taskStatusHistory = new TaskStatusHistory(
        //        TaskStatusEnum.PENDING, LocalDateTime.now(),  LocalDateTime.now()
        //);
    }

}
