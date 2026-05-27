package vn.localhelp.core.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Generic DTO bọc kết quả phân trang cho mọi API list trong LocalHelp.
 *
 * <p>Cấu trúc response trả về client:</p>
 * <pre>
 * {
 *   "meta": { "page": 1, "size": 10, "pages": 5, "total": 48 },
 *   "result": [ ... ]  // kiểu generic T
 * }
 * </pre>
 *
 * <p>Lưu ý: 'page' trong Meta là 1-indexed (bắt đầu từ 1, thân thiện với người dùng),
 * trong khi Spring Pageable dùng 0-indexed nên cần +1 khi build Meta.</p>
 *
 * <p>pages = reviewPage.getTotalPages() (Spring tính tự động = ceil(total / size)).</p>
 *
 * @param <T>  Kiểu dữ liệu của result (vd: List<JobResponse>, List<ReviewResponse>)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultPaginationDTO<T> {
  private Meta meta;
  private T result;

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
