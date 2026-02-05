package com.bib.TrustBuy.system.web.controller.review;

import com.bib.TrustBuy.system.bl.dto.review.ReviewRequest;
import com.bib.TrustBuy.system.bl.dto.review.ReviewResponseDTO;
import com.bib.TrustBuy.system.bl.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 1. Create Review
    @PostMapping("/products/{productId}")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Integer productId,
            @RequestParam Integer userId,
            @Valid @ModelAttribute ReviewRequest request) {
        ReviewResponseDTO response = reviewService.createReview(productId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get Approved Reviews
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<ReviewResponseDTO>> getApprovedReviews(@PathVariable Integer productId) {
        List<ReviewResponseDTO> reviews = reviewService.getApprovedReviewsForProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    // 3. Get User's Reviews
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@RequestParam Integer userId) {
        List<ReviewResponseDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    // 4. Get Review
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId) {
        ReviewResponseDTO response = reviewService.getByIdForUser(reviewId, userId);
        return ResponseEntity.ok(response);
    }

    // 5. Edit Review
    @PostMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewResponseDTO> editReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId,
            @Valid @ModelAttribute ReviewRequest request
    ) {
        ReviewResponseDTO response = reviewService.editReview(reviewId, userId, request);
        return ResponseEntity.ok(response);
    }

    // 6. Delete Review
    @PostMapping("/{reviewId}/delete")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    // 7. List Pending Reviews
    @GetMapping("/admin/pending")
    public ResponseEntity<List<ReviewResponseDTO>> listPendingReviews() {
        List<ReviewResponseDTO> response = reviewService.listPendingReviews();
        return ResponseEntity.ok(response);
    }

    // 8. Approve Review (Admin)
    @PostMapping("/admin/{reviewId}/approve")
    public ResponseEntity<ReviewResponseDTO> approveReview(@PathVariable Integer reviewId) {
        ReviewResponseDTO review = reviewService.approveReview(reviewId);
        return ResponseEntity.ok(review);
    }

    // 9. Reject Review (Admin)
    @PostMapping("/admin/{reviewId}/reject")
    public ResponseEntity<ReviewResponseDTO> rejectReview(@PathVariable Integer reviewId) {
        ReviewResponseDTO review = reviewService.rejectReview(reviewId);
        return ResponseEntity.ok(review);
    }
}
