package vn.localhelp.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.mapper.ReviewMapper;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ResultPaginationDTO getReviewsForUser(Long userId, Pageable pageable) {
        Page<vn.localhelp.core.domain.entity.Review> reviewPage = reviewRepository.findByRevieweeId(userId, pageable);
        
        ResultPaginationDTO.Meta meta = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .pages(reviewPage.getTotalPages())
                .total(reviewPage.getTotalElements())
                .build();

        return ResultPaginationDTO.builder()
                .meta(meta)
                .result(reviewPage.getContent().stream()
                        .map(reviewMapper::toResponse)
                        .toList())
                .build();
    }
}
