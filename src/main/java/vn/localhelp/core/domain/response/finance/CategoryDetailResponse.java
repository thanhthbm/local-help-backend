package vn.localhelp.core.domain.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponse {
    private String categoryName;
    private Double totalAmount;
    private List<SubCategoryDTO> subCategories;
    private List<TransactionItemDTO> transactions;
    private String aiInsight;
}
