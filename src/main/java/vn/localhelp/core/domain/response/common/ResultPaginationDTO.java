package vn.localhelp.core.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultPaginationDTO {
  private Meta meta;
  private Object result;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Meta {
    private int page;
    private int size;
    private int pages;
    private long total;
  }
}
