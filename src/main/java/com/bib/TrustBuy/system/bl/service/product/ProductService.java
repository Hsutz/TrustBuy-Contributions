package com.bib.TrustBuy.system.bl.service.product;

import com.bib.TrustBuy.system.bl.dto.product.ProductResponseDTO;
import com.bib.TrustBuy.system.common.enums.ReviewStatus;
import com.bib.TrustBuy.system.persistence.dao.product.ProductRepo;
import com.bib.TrustBuy.system.persistence.dao.review.ReviewRepository;
import com.bib.TrustBuy.system.persistence.entity.review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final ReviewRepository reviewRepository;

    // convert Product entity to ProductResponseDTO
    private ProductResponseDTO convertToResponse(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        // average rating and review count
        List<Review> approvedReviews = reviewRepository.findByProductIdAndStatusAndDelFlgFalse(product.getId(), ReviewStatus.APPROVED);

        double averageRating = approvedReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        dto.setAverageRating(averageRating);
        dto.setReviewCount(approvedReviews.size());

        return dto;
    }
}