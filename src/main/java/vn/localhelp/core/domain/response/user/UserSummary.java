package vn.localhelp.core.domain.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummary {
  private Long id;
  private String fullName;
  private String avatarUrl;
  private Double reputationScore;
}
