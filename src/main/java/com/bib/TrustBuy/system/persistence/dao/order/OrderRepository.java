package com.bib.TrustBuy.system.persistence.dao.order;

import com.bib.TrustBuy.system.persistence.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Get orders by user
    List<Order> findByUserId(Integer userId);

    // Get orders for a specific seller (seller view)
    @Query("""
                SELECT DISTINCT o FROM Order o
                JOIN o.orderItems oi
                JOIN oi.product p
                JOIN p.business b
                WHERE b.owner.id = :sellerUserId
            """)
    List<Order> findOrdersBySellerUserId(@Param("sellerUserId") Integer sellerUserId);
}