package vn.localhelp.core.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.localhelp.core.domain.response.common.RestResponse;
import vn.localhelp.core.domain.response.error.ErrorResponse;
import vn.localhelp.core.util.constant.ErrorCode;

@RestControllerAdvice
public class GlobalException {
  @ExceptionHandler(value = {
      NoResourceFoundException.class
  })
  public ResponseEntity<RestResponse<Object>> handleEndpointNotFoundException(Exception ex) {
    RestResponse<Object> res = RestResponse.builder()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .error("404 Not Found")
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(value = {
      NotFoundException.class
  })
  public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex){
    RestResponse<Object> res = RestResponse.builder()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .error("The requested resource was not found")
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getStatus(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse response = new ErrorResponse(500, "Lỗi bất định" + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        String errorMessage = "Dữ liệu không hợp lệ";
        if (e.getBindingResult().hasErrors()) {
            errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        }

        ErrorResponse response = new ErrorResponse(400, errorMessage);
        return ResponseEntity.status(400).body(response);
    }
}
