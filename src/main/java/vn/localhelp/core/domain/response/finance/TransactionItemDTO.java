package vn.localhelp.core.domain.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionItemDTO {
    private Long id;
    private String name;
    private String serviceName;
    private Double amount;
    private String status;
    private String dateStr;
    private LocalDateTime dateTime;
    private String iconUrl;
    private String colorCode;
}
