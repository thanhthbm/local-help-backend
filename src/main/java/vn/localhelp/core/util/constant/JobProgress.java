package vn.localhelp.core.util.constant;

import lombok.Getter;

/**
 * Trạng thái tiến trình của một JobApplication.
 *
 * <p>Khi creator hủy công việc, các application liên quan được chuyển sang CANCELLED và
 * lưu thêm Progress để hiển thị lịch sử thay đổi.</p>
 */
@Getter
public enum JobProgress {
    APPLIED("Đã gửi yêu cầu"),
    ACCEPTED("Đã xác nhận"),
    REJECTED("Bị từ chối"),
    ON_THE_WAY("Đang đến"),
    WORKING("Đang làm"),
    PENDING_PAYMENT("Chờ thanh toán"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String description;

    JobProgress(String description) {
        this.description = description;
    }
}
