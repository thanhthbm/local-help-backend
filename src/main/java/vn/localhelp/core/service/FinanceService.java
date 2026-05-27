package vn.localhelp.core.service;

import vn.localhelp.core.domain.response.finance.CategoryDetailResponse;
import vn.localhelp.core.domain.response.finance.FinanceOverviewResponse;

public interface FinanceService {
    /**
     * Tính thống kê tổng quan thu/chi theo tháng cho người dùng hiện tại.
     */
    FinanceOverviewResponse getFinanceOverview(String currentUid, String type, int month, int year);

    /**
     * Tính thống kê chi tiết cho một danh mục cụ thể trong tháng.
     */
    CategoryDetailResponse getCategoryDetails(String currentUid, Long categoryId, String type, int month, int year);
}
