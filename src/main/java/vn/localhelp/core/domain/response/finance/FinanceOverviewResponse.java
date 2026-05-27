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
public class FinanceOverviewResponse {
    /** Tổng số tiền thu hoặc chi trong tháng được chọn. */
    private Double totalAmount;
    /** Phần trăm thay đổi so với tháng trước, đã làm tròn ở service. */
    private Double percentageChange;
    /** Xu hướng tăng/giảm: "UP" hoặc "DOWN". */
    private String trend; // "UP" or "DOWN"
    /** Dữ liệu 4 cột biểu đồ tuần, đã chuẩn hóa về khoảng 0..1. */
    private List<Double> weeklyChart; // 4 weeks
    /** Danh sách danh mục đóng góp vào tổng thu/chi. */
    private List<CategoryItemDTO> categories;
    /** Các giao dịch gần đây, thực chất được map từ các job hoàn thành. */
    private List<TransactionItemDTO> recentTransactions;
}
