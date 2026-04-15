package vn.localhelp.core.domain.response.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceOverviewResponse {
    private Double totalAmount;
    private Double percentageChange;
    private String trend; // "UP" or "DOWN"
    private List<Double> weeklyChart; // 4 weeks
    private List<CategoryItemDTO> categories;
    private List<TransactionItemDTO> recentTransactions;
}
