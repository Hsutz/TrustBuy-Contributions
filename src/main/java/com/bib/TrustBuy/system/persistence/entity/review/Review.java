package com.bib.TrustBuy.system.persistence.entity.review;

import com.bib.TrustBuy.system.common.enums.ReviewStatus;
import com.bib.TrustBuy.system.persistence.entity.Product;
import com.bib.TrustBuy.system.persistence.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews", uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id", "user_id"})})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String comment;

    private Integer rating;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    private boolean delFlg = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer createdUser;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer updatedUser;

}
