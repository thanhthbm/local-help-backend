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
    /** Id job, đồng thời là id giao dịch dùng để xem chi tiết. */
    private Long id;
    /** Tên giao dịch, lấy từ title của job. */
    private String name;
    /** Tên dịch vụ/danh mục của job. */
    private String serviceName;
    /** Số tiền của job. */
    private Double amount;
    /** Trạng thái job, thường là COMPLETED trong thống kê. */
    private String status;
    /** Chuỗi ngày giờ đã format để app hiển thị nhanh. */
    private String dateStr;
    /** Thời điểm tạo job dạng LocalDateTime cho các nhu cầu xử lý chi tiết. */
    private LocalDateTime dateTime;
    private String iconUrl;
    /** Màu danh mục đi kèm giao dịch. */
    private String colorCode;
}
