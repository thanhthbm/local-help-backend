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
    /** Tên nhóm phụ, hiện lấy từ title của job. */
    private String subName;
    /** Tổng tiền của nhóm phụ. */
    private Double amount;
    /** Tỷ trọng của nhóm phụ trong danh mục. */
    private Double percentage;
    /** Màu dùng để vẽ donut chart. */
    private String colorCode;
}
