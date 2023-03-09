package MaintenanceManager.MaintenanceManager.models.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailContent {
    private String recipient;
    private String msgBody;
    private String subject;
}
