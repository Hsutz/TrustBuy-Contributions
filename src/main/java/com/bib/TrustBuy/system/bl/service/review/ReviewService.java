package com.bib.TrustBuy.system.bl.service.review;

import com.bib.TrustBuy.system.bl.dto.review.ReviewRequest;
import com.bib.TrustBuy.system.bl.dto.review.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(Integer productId, Integer userId, ReviewRequest request);

    List<ReviewResponseDTO> getApprovedReviewsForProduct(Integer productId);

    ReviewResponseDTO getByIdForUser(Integer reviewId, Integer userId);

    ReviewResponseDTO editReview(Integer reviewId, Integer userId, ReviewRequest request);

    void deleteReview(Integer reviewId, Integer userId);

    List<ReviewResponseDTO> getUserReviews(Integer userId);

    List<ReviewResponseDTO> listPendingReviews();

    ReviewResponseDTO approveReview(Integer reviewId);

    ReviewResponseDTO rejectReview(Integer reviewId);

}

