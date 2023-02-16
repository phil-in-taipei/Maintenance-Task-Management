package MaintenanceManager.MaintenanceManager.models;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING) // possibly move to main table
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

    TaskStatusHistory(
            TaskStatusEnum status, LocalDateTime createdDateTime,
            LocalDateTime updatedDateTime) {
        this.status = status;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
        this.timesModified = 1;
    };
}
