package com.bib.TrustBuy.system.bl.service.business;

import com.bib.TrustBuy.system.bl.dto.business.BusinessResponseDTO;
import com.bib.TrustBuy.system.common.enums.ReviewStatus;
import com.bib.TrustBuy.system.persistence.dao.product.ProductRepo;
import com.bib.TrustBuy.system.persistence.dao.review.ReviewRepository;
import com.bib.TrustBuy.system.persistence.entity.Business;
import com.bib.TrustBuy.system.persistence.entity.Product;
import com.bib.TrustBuy.system.persistence.entity.review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final ProductRepo productRepository;
    private final ReviewRepository reviewRepository;

    private BusinessResponseDTO convertToDTO(Business b) {
        BusinessResponseDTO dto = new BusinessResponseDTO();

        // Average rating and review count across all products
        List<Product> products = productRepository.findByBusinessIdAndDelFlgFalse(b.getId());

        List<Review> approvedReviews = products.stream()
                .flatMap(product -> reviewRepository.findByProductIdAndStatusAndDelFlgFalse(product.getId(), ReviewStatus.APPROVED).stream())
                .toList();

        double avgRating = approvedReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        dto.setAverageRating(avgRating);
        dto.setReviewCount(approvedReviews.size());

        return dto;
    }
}
