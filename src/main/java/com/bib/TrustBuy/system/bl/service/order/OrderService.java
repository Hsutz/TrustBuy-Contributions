package com.bib.TrustBuy.system.bl.service.order;

import com.bib.TrustBuy.system.bl.dto.order.OrderDetailsDTO;
import com.bib.TrustBuy.system.bl.dto.order.OrderSummaryDTO;
import com.bib.TrustBuy.system.common.enums.OrderStatus;
import com.bib.TrustBuy.system.persistence.entity.Role;

import java.util.List;

public interface OrderService {
    // Checkout selected cart items into an order
    OrderSummaryDTO checkoutOrder(Integer userId);

    // Get all orders for a specific user
    List<OrderSummaryDTO> getUserOrders(Integer userId);

    // Get all orders for a specific seller (seller view)
    List<OrderSummaryDTO> getSellerOrders(Integer sellerUserId);

    // Get all orders (admin view)
    List<OrderSummaryDTO> getAllOrders();

    // Get detailed info for a specific order for a user
    OrderDetailsDTO getOrderDetailsForUser(Integer orderId, Integer userId);

    // Get detailed info for a specific order for a seller
    OrderDetailsDTO getOrderDetailsForSeller(Integer orderId, Integer sellerUserId);

    // Update the status of an order (e.g. shipped, cancelled)
    OrderDetailsDTO updateOrderStatus(Integer orderId, OrderStatus newStatus, Integer actorId, Role actorRole);

}
