package com.bib.TrustBuy.system.persistence.entity.order;

import com.bib.TrustBuy.system.common.enums.OrderStatus;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.cart.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Order")
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean delFlg = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer createdUser;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Integer updatedUser;

    public Order(User user, List<CartItem> selectedItems) {
        this.user = user;

        // convert cart items into order items
        selectedItems.forEach(cartItem -> {
            OrderItem item = new OrderItem(this, cartItem.getProduct(), cartItem.getQuantity(), cartItem.getPrice());
            this.orderItems.add(item);
        });
//        this.orderItems = new ArrayList<>();
//        selectedItems.forEach(cartItem -> {
//            OrderItem item = new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getPrice());
//            item.setOrder(this);
//            this.orderItems.add(item);   // Hibernate-managed list
//        });

        // snapshot total amount from order items
        this.totalAmount = this.orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
