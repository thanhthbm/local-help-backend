package vn.localhelp.core.util.error;

import lombok.Getter;
import vn.localhelp.core.util.constant.ErrorCode;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
