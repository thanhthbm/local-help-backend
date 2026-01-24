package vn.localhelp.core.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
  private int statusCode;
  private String error;
  private Object message;
  private T data;
}
