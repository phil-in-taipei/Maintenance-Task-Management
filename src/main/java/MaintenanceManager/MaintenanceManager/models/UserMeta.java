package MaintenanceManager.MaintenanceManager.models;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String givenName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer age;
}
