package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.localhelp.core.domain.response.finance.CategoryDetailResponse;
import vn.localhelp.core.domain.response.finance.FinanceOverviewResponse;
import vn.localhelp.core.service.FinanceService;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /**
     * Lấy thống kê tổng quan thu/chi của người dùng trong một tháng.
     *
     * type = "spending" nghĩa là thống kê các job user tạo và đã hoàn thành.
     * type = "earning" nghĩa là thống kê các job user làm helper và đã hoàn thành.
     */
    @GetMapping("/overview")
    @ApiMessage("Lấy thống kê tổng quan thành công")
    public ResponseEntity<FinanceOverviewResponse> getFinanceOverview(
            @RequestParam String type,
            @RequestParam int month,
            @RequestParam int year
    ) {
        String currentUid = FirebaseUtil.getCurrentUserUid();
        return ResponseEntity.ok(financeService.getFinanceOverview(currentUid, type, month, year));
    }

    /**
     * Lấy thống kê chi tiết của một danh mục trong tháng.
     *
     * API này phục vụ màn CategoryDetailScreen trên app sau khi người dùng
     * bấm vào một dòng danh mục trong màn thống kê.
     */
    @GetMapping("/categories/{categoryId}/details")
    @ApiMessage("Lấy chi tiết danh mục thành công")
    public ResponseEntity<CategoryDetailResponse> getCategoryDetails(
            @PathVariable Long categoryId,
            @RequestParam String type,
            @RequestParam int month,
            @RequestParam int year
    ) {
        String currentUid = FirebaseUtil.getCurrentUserUid();
        return ResponseEntity.ok(financeService.getCategoryDetails(currentUid, categoryId, type, month, year));
    }
}
