package vn.localhelp.core.domain.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {
    private String subName;
    private Double amount;
    private Double percentage;
    private String colorCode;
}
