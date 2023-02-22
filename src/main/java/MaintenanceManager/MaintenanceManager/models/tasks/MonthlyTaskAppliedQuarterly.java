package MaintenanceManager.MaintenanceManager.models.tasks;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTaskAppliedQuarterly {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuarterlySchedulingEnum quarter;

    @Column(nullable = false)
    @NotNull(message = "A Year must be provided")
    @Min(
            value = 2023,
            message = "Prior to 2023 cannot be arranged"
    )
    @Max(
            value = 2025,
            message = "After 2025 cannot be arranged"
    )
    private Integer year;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private MonthlyTaskScheduler monthlyTaskScheduler;

}
