package com.bib.TrustBuy.system.persistence.dao.review;

import com.bib.TrustBuy.system.common.enums.ReviewStatus;
import com.bib.TrustBuy.system.persistence.entity.Product;
import com.bib.TrustBuy.system.persistence.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndProductId(Integer userId, Integer productId);

    List<Review> findByProductIdAndStatusAndDelFlgFalse(Integer productId, ReviewStatus status);

    Optional<Review> findByIdAndStatus(Integer reviewId, ReviewStatus status);

    Optional<Review> findById(Integer reviewId);

    List<Review> findByUserIdAndDelFlgFalse(Integer userId);

    List<Review> findByStatus(ReviewStatus status);

    List<Review> findByStatusAndDelFlgFalse(ReviewStatus reviewStatus);
//
//    int countByUserId(Integer userId);
//
//    int countByProductIn(List<Product> products);
//
//    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product IN :products")
//    BigDecimal calculateAverageRatingByProducts(@Param("products") List<Product> products);
}