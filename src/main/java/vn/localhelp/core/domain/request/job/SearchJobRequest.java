package vn.localhelp.core.domain.request.job;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchJobRequest {
    private Long userId;

    @NotNull
    private Integer page;
    @NotNull
    private Integer size;

    @NotNull(message = "Vui lòng nhập khoảng cách tìm kiếm")
    @Min(value = 1, message = "Khoảng cách tối thiểu là 1km")
    private Double maxDistance;

    @Min(value = 0, message = "Thù lao không được âm")
    private Double minSalary = 0.0;

    private List<Long> categoryIds;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Thiếu kinh độ")
    private Double longitude;

    @NotNull(message = "Thiếu vĩ độ")
    private Double latitude;

    @NotBlank(message = "không có keyword à ◉_◉")
    private String keyword;
}
