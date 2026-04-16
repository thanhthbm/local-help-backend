package vn.localhelp.core.domain.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;
}
