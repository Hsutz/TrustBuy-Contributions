package com.bib.TrustBuy.system.web.controller.order;

import com.bib.TrustBuy.system.bl.dto.order.OrderDetailsDTO;
import com.bib.TrustBuy.system.bl.dto.order.OrderSummaryDTO;
import com.bib.TrustBuy.system.bl.dto.order.UpdateOrderStatusRequest;
import com.bib.TrustBuy.system.bl.service.order.OrderService;
import com.bib.TrustBuy.system.common.enums.OrderStatus;
import com.bib.TrustBuy.system.persistence.dao.role.RoleRepository;
import com.bib.TrustBuy.system.persistence.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final RoleRepository roleRepository;

    // 1. Checkout Order
    @PostMapping("/checkout")
    public ResponseEntity<OrderSummaryDTO> checkoutOrder(@RequestParam Integer userId) {
        OrderSummaryDTO order = orderService.checkoutOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // 2. Get User Orders
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryDTO>> getUserOrders(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    // 3. Get Seller Orders
    @GetMapping("/seller/{sellerUserId}")
    public ResponseEntity<List<OrderSummaryDTO>> getSellerOrders(@PathVariable Integer sellerUserId) {
        return ResponseEntity.ok(orderService.getSellerOrders(sellerUserId));
    }

    // 4. Get All Orders (Admin)
    @GetMapping("/admin")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 5. Get Order Details
    @GetMapping("/user/{userId}/{orderId}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetailsForUser(
            @PathVariable Integer userId,
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailsForUser(orderId, userId));
    }

    @GetMapping("/seller/{sellerUserId}/{orderId}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetailsForSeller(
            @PathVariable Integer orderId,
            @PathVariable Integer sellerUserId) {
        return ResponseEntity.ok(orderService.getOrderDetailsForSeller(orderId, sellerUserId));
    }

    // 6. Update Order Status
    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<OrderDetailsDTO> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }

        Role actorRole = roleRepository.findByRoleName(request.getActorRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getActorRole()));

        OrderDetailsDTO updatedOrder = orderService.updateOrderStatus(orderId, newStatus, request.getActorId(), actorRole);

        return ResponseEntity.ok(updatedOrder);
    }

}

