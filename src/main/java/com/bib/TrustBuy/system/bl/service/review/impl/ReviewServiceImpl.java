package com.bib.TrustBuy.system.bl.service.review.impl;

import com.bib.TrustBuy.system.bl.dto.review.ReviewRequest;
import com.bib.TrustBuy.system.bl.dto.review.ReviewResponseDTO;
import com.bib.TrustBuy.system.bl.service.review.FileStorageService;
import com.bib.TrustBuy.system.bl.service.review.ReviewService;
import com.bib.TrustBuy.system.common.enums.ReviewStatus;
import com.bib.TrustBuy.system.exception.ForbiddenException;
import com.bib.TrustBuy.system.exception.ResourceNotFoundException;
import com.bib.TrustBuy.system.persistence.dao.product.ProductRepo;
import com.bib.TrustBuy.system.persistence.dao.review.ReviewRepository;
import com.bib.TrustBuy.system.persistence.dao.user.UserRepository;
import com.bib.TrustBuy.system.persistence.entity.Product;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepo productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // Create a Review
    @Override
    public ReviewResponseDTO createReview(Integer productId, Integer userId, ReviewRequest request) {
        // Check if user already reviewed this product
        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new IllegalStateException("User has already reviewed this product");
        }

        Product product = productRepository.findById(productId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setStatus(ReviewStatus.PENDING); // moderation default
        review.setCreatedUser(userId);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = fileStorageService.store(request.getImage());
                review.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        reviewRepository.save(review);

        return toDto(review);
    }

    // Get All Approved Reviews
    @Override
    public List<ReviewResponseDTO> getApprovedReviewsForProduct(Integer productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndStatusAndDelFlgFalse(productId, ReviewStatus.APPROVED);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Get User's Own Reviews
    @Override
    public List<ReviewResponseDTO> getUserReviews(Integer userId) {
        List<Review> reviews = reviewRepository.findByUserIdAndDelFlgFalse(userId);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Get Review
    public ReviewResponseDTO getByIdForUser(Integer reviewId, Integer userId) {
        Review review = reviewRepository.findByIdAndStatus(reviewId, ReviewStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own review");
        }

        return toDto(review);
    }

    // Edit a Review
    @Override
    public ReviewResponseDTO editReview(Integer reviewId, Integer userId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndStatus(reviewId, ReviewStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own review");
        }

        review.setComment(request.getComment());
        review.setRating(request.getRating());
        review.setStatus(ReviewStatus.PENDING);
        review.setUpdatedUser(userId);

        if (request.getRemoveImage()) {
            review.setImageUrl(null);
        } else if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                String imageUrl = fileStorageService.store(request.getImage());
                review.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }

        reviewRepository.save(review);

        return toDto(review);
    }

    // Delete a Review
    public void deleteReview(Integer reviewId, Integer userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own review");
        }

        review.setDelFlg(true);
        review.setUpdatedUser(userId);

        reviewRepository.save(review);
    }

    // List All Pending Reviews
    @Override
    public List<ReviewResponseDTO> listPendingReviews() {
        List<Review> reviews = reviewRepository.findByStatusAndDelFlgFalse(ReviewStatus.PENDING);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Approve a Review
    @Override
    public ReviewResponseDTO approveReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.isDelFlg()) {
            throw new IllegalStateException("Cannot approve a deleted review");
        }

        review.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(review);

        return toDto(review);
    }

    // Reject a Review
    @Override
    public ReviewResponseDTO rejectReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.isDelFlg()) {
            throw new IllegalStateException("Cannot reject a deleted review");
        }

        review.setStatus(ReviewStatus.REJECTED);
        reviewRepository.save(review);

        return toDto(review);
    }

    // Helper
    private ReviewResponseDTO toDto(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getComment(),
                review.getRating(),
                review.getImageUrl(),
                review.getStatus().name(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
