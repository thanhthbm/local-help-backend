package vn.localhelp.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.response.finance.*;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.service.FinanceService;
import vn.localhelp.core.util.constant.JobStatus;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    /**
     * Tính dữ liệu tổng quan cho màn thống kê thu/chi.
     *
     * Hàm lấy user hiện tại từ Firebase UID, lọc job COMPLETED theo tháng,
     * so sánh với tháng trước, rồi gom dữ liệu thành biểu đồ tuần, danh mục
     * và danh sách giao dịch gần đây.
     */
    @Override
    public FinanceOverviewResponse getFinanceOverview(String currentUid, String type, int month, int year) {
        User user = userRepository.findByFirebaseUid(currentUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Job> currentMonthJobs = getJobsByType(user.getId(), type, startOfMonth, endOfMonth);

        // Previous month data for percentage change
        YearMonth prevYearMonth = yearMonth.minusMonths(1);
        LocalDateTime prevMonthStart = prevYearMonth.atDay(1).atStartOfDay();
        LocalDateTime prevMonthEnd = prevYearMonth.atEndOfMonth().atTime(23, 59, 59);
        List<Job> prevMonthJobs = getJobsByType(user.getId(), type, prevMonthStart, prevMonthEnd);

        double currentTotal = currentMonthJobs.stream().mapToDouble(Job::getPrice).sum();
        double prevTotal = prevMonthJobs.stream().mapToDouble(Job::getPrice).sum();

        double percentageChange = 0.0;
        String trend = "UP";
        if (prevTotal > 0) {
            percentageChange = ((currentTotal - prevTotal) / prevTotal) * 100;
        } else if (currentTotal > 0) {
            percentageChange = 100.0;
        }

        if (percentageChange < 0) {
            trend = "DOWN";
            percentageChange = Math.abs(percentageChange);
        }

        List<Double> weeklyChart = calculateWeeklyChart(currentMonthJobs, startOfMonth, endOfMonth);
        List<CategoryItemDTO> categoryItems = calculateTopCategories(currentMonthJobs, currentTotal);
        List<TransactionItemDTO> transactions = mapToRecentTransactions(currentMonthJobs);

        return FinanceOverviewResponse.builder()
                .totalAmount(currentTotal)
                .percentageChange((double) Math.round(percentageChange * 10) / 10)
                .trend(trend)
                .weeklyChart(weeklyChart)
                .categories(categoryItems)
                .recentTransactions(transactions)
                .build();
    }

    /**
     * Tính dữ liệu chi tiết của một danh mục trong màn thống kê.
     *
     * Sau khi lọc theo type và tháng, dữ liệu được lọc tiếp theo categoryId,
     * rồi gom nhóm phụ theo title vì hệ thống hiện chưa có bảng sub-category riêng.
     */
    @Override
    public CategoryDetailResponse getCategoryDetails(String currentUid, Long categoryId, String type, int month, int year) {
        User user = userRepository.findByFirebaseUid(currentUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Job> jobs = getJobsByType(user.getId(), type, startOfMonth, endOfMonth)
                .stream()
                .filter(job -> job.getCategory() != null && job.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

        double totalAmount = jobs.stream().mapToDouble(Job::getPrice).sum();

        String categoryName = jobs.isEmpty() ? "Danh mục" : jobs.get(0).getCategory().getName();

        List<SubCategoryDTO> subCategories = calculateSubCategories(jobs, totalAmount);
        List<TransactionItemDTO> transactions = mapToRecentTransactions(jobs);

        String aiInsight;
        if (type.equals("earning")) {
            aiInsight = String.format("Bạn kiếm được lớn nhất từ nhóm '%s'. Hãy tăng cường nhận các dịch vụ liên quan để cải thiện thu nhập.",
                    subCategories.isEmpty() ? categoryName : subCategories.get(0).getSubName());
        } else {
            aiInsight = String.format("Bạn đã chi nhiều nhất ở '%s'. Vui lòng lưu ý tối ưu ngân sách nếu vượt quá dự định.",
                    subCategories.isEmpty() ? categoryName : subCategories.get(0).getSubName());
        }

        return CategoryDetailResponse.builder()
                .categoryName(categoryName)
                .totalAmount(totalAmount)
                .subCategories(subCategories)
                .transactions(transactions)
                .aiInsight(aiInsight)
                .build();
    }

    /**
     * Chọn tập job theo vai trò của user trong giao dịch.
     *
     * earning: user là helper nhận tiền. spending: user là creator trả tiền.
     */
    private List<Job> getJobsByType(Long userId, String type, LocalDateTime start, LocalDateTime end) {
        if ("earning".equalsIgnoreCase(type)) {
            return jobRepository.findByHelperIdAndJobStatusAndCreatedAtBetween(userId, JobStatus.COMPLETED, start, end);
        } else {
            return jobRepository.findByCreatorIdAndJobStatusAndCreatedAtBetween(userId, JobStatus.COMPLETED, start, end);
        }
    }

    /**
     * Chuẩn hóa tổng tiền theo 4 tuần trong tháng thành giá trị 0..1 để app vẽ bar chart.
     */
    private List<Double> calculateWeeklyChart(List<Job> jobs, LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        double week1 = 0, week2 = 0, week3 = 0, week4 = 0;
        int daysInMonth = endOfMonth.getDayOfMonth();
        int midDay = daysInMonth / 2;

        for (Job job : jobs) {
            int day = job.getCreatedAt().getDayOfMonth();
            if (day <= 7) week1 += job.getPrice();
            else if (day <= 14) week2 += job.getPrice();
            else if (day <= 21) week3 += job.getPrice();
            else week4 += job.getPrice();
        }
        
        double max = Math.max(Math.max(week1, week2), Math.max(week3, week4));
        if (max == 0) max = 1; // prevent divide by zero
        // Returns percentage (0 to 1) for the bar chart
        return Arrays.asList(week1 / max, week2 / max, week3 / max, week4 / max);
    }

    /**
     * Gom job theo danh mục và tính tỷ trọng từng danh mục trên tổng thu/chi.
     */
    private List<CategoryItemDTO> calculateTopCategories(List<Job> jobs, double totalAmount) {
        Map<Long, List<Job>> grouped = jobs.stream()
                .filter(j -> j.getCategory() != null)
                .collect(Collectors.groupingBy(j -> j.getCategory().getId()));

        List<CategoryItemDTO> result = new ArrayList<>();
        for (Map.Entry<Long, List<Job>> entry : grouped.entrySet()) {
            List<Job> catJobs = entry.getValue();
            double cTotal = catJobs.stream().mapToDouble(Job::getPrice).sum();
            int percentage = totalAmount > 0 ? (int) Math.round((cTotal / totalAmount) * 100) : 0;
            
            vn.localhelp.core.domain.entity.Category category = catJobs.get(0).getCategory();

            result.add(CategoryItemDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .amount(cTotal)
                    .percentage((double) percentage)
                    .iconUrl(category.getIconUrl())
                    .colorCode(category.getColorCode() != null ? category.getColorCode() : "#4A90D9")
                    .build());
        }

        result.sort((c1, c2) -> Double.compare(c2.getAmount(), c1.getAmount()));
        return result;
    }

    /**
     * Tạo nhóm phụ cho màn chi tiết danh mục.
     *
     * Vì chưa có entity SubCategory, hệ thống tạm nhóm các job theo title.
     */
    private List<SubCategoryDTO> calculateSubCategories(List<Job> jobs, double totalAmount) {
        // Since there is no explicit sub-category, we group by job title to simulate sub-categories.
        Map<String, List<Job>> grouped = jobs.stream()
                .filter(j -> j.getTitle() != null)
                .collect(Collectors.groupingBy(Job::getTitle));

        List<SubCategoryDTO> result = new ArrayList<>();
        String[] colors = {"#F0A040", "#4A90D9", "#9B59B6", "#2E9B5B", "#E06080"};
        int i = 0;
        
        for (Map.Entry<String, List<Job>> entry : grouped.entrySet()) {
            double sTotal = entry.getValue().stream().mapToDouble(Job::getPrice).sum();
            int percentage = totalAmount > 0 ? (int) Math.round((sTotal / totalAmount) * 100) : 0;
            
            result.add(SubCategoryDTO.builder()
                    .subName(entry.getKey())
                    .amount(sTotal)
                    .percentage((double) percentage)
                    .colorCode(colors[i % colors.length])
                    .build());
            i++;
        }

        result.sort((s1, s2) -> Double.compare(s2.getAmount(), s1.getAmount()));
        return result;
    }

    /**
     * Chuyển danh sách job thành các dòng giao dịch gần đây hiển thị trên app.
     */
    private List<TransactionItemDTO> mapToRecentTransactions(List<Job> jobs) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'Th'MM, HH:mm");
        return jobs.stream()
                .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
                .limit(5)
                .map(job -> TransactionItemDTO.builder()
                        .id(job.getId())
                        .name(job.getTitle() != null ? job.getTitle() : "Dịch vụ")
                        .serviceName(job.getCategory() != null ? job.getCategory().getName() : "")
                        .amount(job.getPrice())
                        .status(job.getJobStatus().name())
                        .dateStr(job.getCreatedAt().format(formatter))
                        .dateTime(job.getCreatedAt())
                        .colorCode(job.getCategory() != null ? job.getCategory().getColorCode() : "#4A90D9")
                        .build())
                .collect(Collectors.toList());
    }
}
