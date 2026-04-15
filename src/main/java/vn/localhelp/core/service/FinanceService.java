package vn.localhelp.core.service;

import vn.localhelp.core.domain.response.finance.CategoryDetailResponse;
import vn.localhelp.core.domain.response.finance.FinanceOverviewResponse;

public interface FinanceService {
    FinanceOverviewResponse getFinanceOverview(String currentUid, String type, int month, int year);
    CategoryDetailResponse getCategoryDetails(String currentUid, Long categoryId, String type, int month, int year);
}
