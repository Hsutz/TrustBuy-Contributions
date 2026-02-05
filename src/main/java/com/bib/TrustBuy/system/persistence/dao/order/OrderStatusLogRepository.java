package com.bib.TrustBuy.system.persistence.dao.order;

import com.bib.TrustBuy.system.persistence.entity.order.OrderStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, Integer> {
    List<OrderStatusLog> findByOrderId(Integer orderId);
}
