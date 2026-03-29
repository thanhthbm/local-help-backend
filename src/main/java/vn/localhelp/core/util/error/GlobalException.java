package vn.localhelp.core.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.localhelp.core.domain.response.common.RestResponse;

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

}
