package vn.localhelp.core.util.constant;

/**
 * Trạng thái vòng đời của một công việc.
 *
 * <p>Các chức năng đăng, cập nhật và hủy công việc dùng OPEN/CANCELLED để kiểm tra
 * điều kiện nghiệp vụ: job mới tạo ở trạng thái OPEN, chỉ job OPEN được cập nhật hoặc hủy.</p>
 */
public enum JobStatus {
    OPEN,
    ACCEPTED,
    ON_THE_WAY,
    WORKING,
    PENDING_PAYMENT,
    COMPLETED,
    CANCELLED,
    APPLIED,
    REJECTED
}
