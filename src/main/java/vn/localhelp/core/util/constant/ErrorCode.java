package vn.localhelp.core.util.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_PARAM(400, "Tham số đầu vào không hợp lệ"),
    JOB_NOT_FOUND(404, "Không tìm thấy công việc"),
    INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ nội bộ"),
    USER_NOT_FOUND(404, "Không tìm thấy người dùng"),
    JOB_NOT_OPEN(400, "Công việc này đã đóng hoặc đã có người nhận"),
    CANNOT_APPLY_OWN_JOB(400, "Bạn không thể tự nhận công việc của chính mình"),
    ALREADY_APPLIED(400, "Bạn đã ứng tuyển công việc này rồi, vui lòng chờ xác nhận");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
