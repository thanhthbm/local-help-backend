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
    /** Tên danh mục đang xem chi tiết. */
    private String categoryName;
    /** Tổng tiền của danh mục trong tháng. */
    private Double totalAmount;
    /** Các nhóm phụ, hiện được mô phỏng bằng cách gom theo job title. */
    private List<SubCategoryDTO> subCategories;
    /** Danh sách giao dịch thuộc danh mục. */
    private List<TransactionItemDTO> transactions;
    /** Nhận xét ngắn để hiển thị trong card gợi ý trên app. */
    private String aiInsight;
}
