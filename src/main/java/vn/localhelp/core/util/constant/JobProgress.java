package vn.localhelp.core.util.constant;

import lombok.Getter;

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
