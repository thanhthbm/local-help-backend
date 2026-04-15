package vn.localhelp.core.domain.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemDTO {
    private Long id;
    private String name;
    private String iconUrl;
    private String colorCode;
    private Double amount;
    private Double percentage;
}
