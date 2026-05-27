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
    /** Id danh mục để app điều hướng sang màn chi tiết. */
    private Long id;
    /** Tên danh mục hiển thị. */
    private String name;
    /** Icon danh mục nếu có. */
    private String iconUrl;
    /** Màu danh mục dùng cho progress bar/chart. */
    private String colorCode;
    /** Tổng tiền của danh mục trong tháng. */
    private Double amount;
    /** Tỷ trọng của danh mục so với tổng thu/chi. */
    private Double percentage;
}
