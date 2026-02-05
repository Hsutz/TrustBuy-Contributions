package com.bib.TrustBuy.system.persistence.entity.order;

import com.bib.TrustBuy.system.common.enums.OrderStatus;
import com.bib.TrustBuy.system.persistence.entity.Business;
import com.bib.TrustBuy.system.persistence.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderItem")
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean delFlg = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer createdUser;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer updatedUser;

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.business = product.getBusiness();
        this.quantity = quantity;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.delFlg = false;
    }
}
