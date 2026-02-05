package com.bib.TrustBuy.system.bl.service.order.impl;

import com.bib.TrustBuy.system.bl.dto.order.OrderDetailsDTO;
import com.bib.TrustBuy.system.bl.dto.order.OrderItemDTO;
import com.bib.TrustBuy.system.bl.dto.order.OrderSummaryDTO;
import com.bib.TrustBuy.system.bl.service.order.NotificationService;
import com.bib.TrustBuy.system.bl.service.order.OrderService;
import com.bib.TrustBuy.system.common.enums.OrderStatus;
import com.bib.TrustBuy.system.exception.PermissionDeniedException;
import com.bib.TrustBuy.system.persistence.dao.cart.CartRepository;
import com.bib.TrustBuy.system.persistence.dao.order.OrderRepository;
import com.bib.TrustBuy.system.persistence.dao.order.OrderStatusLogRepository;
import com.bib.TrustBuy.system.persistence.dao.product.ProductRepo;
import com.bib.TrustBuy.system.persistence.dao.user.UserRepository;
import com.bib.TrustBuy.system.persistence.entity.Product;
import com.bib.TrustBuy.system.persistence.entity.Role;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.cart.Cart;
import com.bib.TrustBuy.system.persistence.entity.cart.CartItem;
import com.bib.TrustBuy.system.persistence.entity.order.Order;
import com.bib.TrustBuy.system.persistence.entity.order.OrderItem;
import com.bib.TrustBuy.system.persistence.entity.order.OrderStatusLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderStatusLogRepository orderStatusLogRepository;
    private final NotificationService notificationService;
    private final ProductRepo productRepository;

    // Checkout selected cart items into an order
    @Override
    @Transactional
    public OrderSummaryDTO checkoutOrder(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> item.getSelected() && !item.isDelFlg())
                .collect(Collectors.toList());

        System.out.println("Selected items count: " + selectedItems.size());
        selectedItems.forEach(i ->
                System.out.println("Selected: " + i.getProduct().getTitle())
        );

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("No selected items to checkout");
        }

        // Validate stock
        for (CartItem item : selectedItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for " + product.getTitle());
            }
        }

        // Deduct stock
        for (CartItem item : selectedItems) {
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow();
            product.setStock(product.getStock() - item.getQuantity());
            product.setUpdatedUser(userId);
            productRepository.save(product);
        }

        // Mark selected items as removed from cart
        selectedItems.forEach(item -> {
            item.setDelFlg(true);
            item.setUpdatedUser(userId);
        });
        cartRepository.save(cart);

        // Create order
        Order order = new Order(user, selectedItems);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedUser(userId);
        order.setUpdatedUser(userId);

        Order saved = orderRepository.save(order);

        return toOrderSummaryDTO(saved);
    }

    // Get all orders for a specific user
    @Override
    public List<OrderSummaryDTO> getUserOrders(Integer userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toOrderSummaryDTO)
                .sorted(Comparator.comparing(OrderSummaryDTO::getCreatedAt).reversed())
                .toList();
    }

    // Get all orders for a specific seller (seller view)
    @Override
    public List<OrderSummaryDTO> getSellerOrders(Integer sellerUserId) {
        return orderRepository.findOrdersBySellerUserId(sellerUserId).stream()
                .map(order -> {
                    OrderSummaryDTO dto = toOrderSummaryDTO(order);
                    dto.setStatus(null);
                    List<OrderItem> sellerItems = order.getOrderItems().stream()
                            .filter(item -> item.getProduct().getBusiness().getOwner().getId().equals(sellerUserId))
                            .toList();
                    OrderStatus sellerStatus = sellerItems.isEmpty() ? null : sellerItems.getFirst().getStatus();
                    dto.setSellerStatus(sellerStatus);
                    return dto;
                })
                .sorted(Comparator.comparing(OrderSummaryDTO::getCreatedAt).reversed())
                .toList();
    }

    // Get all orders (admin view)
    @Override
    public List<OrderSummaryDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toOrderSummaryDTO)
                .sorted(Comparator.comparing(OrderSummaryDTO::getCreatedAt).reversed())
                .toList();
    }

    // Get detailed info for a specific order for a user
    @Override
    public OrderDetailsDTO getOrderDetailsForUser(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new PermissionDeniedException("Not allowed to view this order");
        }
        return toOrderDetailsDTO(order);
    }

    // Get detailed info for a specific order for a seller
    @Override
    public OrderDetailsDTO getOrderDetailsForSeller(Integer orderId, Integer sellerUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailsDTO dto = toOrderDetailsDTO(order);
        dto.setStatus(null);

        List<OrderItemDTO> filteredItems = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getBusiness().getOwner().getId().equals(sellerUserId))
                .map(item -> {
                    OrderItemDTO dtoItem = new OrderItemDTO();
                    dtoItem.setProductName(item.getProduct().getTitle());
                    dtoItem.setQuantity(item.getQuantity());
                    dtoItem.setPrice(item.getPrice());
                    dtoItem.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    dtoItem.setBusinessId(item.getProduct().getBusiness().getId());
                    dtoItem.setStatus(item.getStatus());
                    return dtoItem;
                })
                .toList();

        if (filteredItems.isEmpty()) {
            throw new PermissionDeniedException("Seller not allowed to view this order");
        }

        dto.setOrderItems(filteredItems);

        OrderStatus sellerStatus = filteredItems.getFirst().getStatus();
        dto.setSellerStatus(sellerStatus);

        return dto;
    }

    // Update the status of an order
    @Override
    public OrderDetailsDTO updateOrderStatus(Integer orderId, OrderStatus newStatus, Integer actorId, Role actorRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        OrderStatus currentStatus = order.getStatus();

        // User rules
        if ("ROLE_USER".equals(actorRole.getRoleName())) {
            if (newStatus != OrderStatus.CANCELLED) {
                throw new PermissionDeniedException("User can only cancel orders");
            }
            if (currentStatus != OrderStatus.PENDING) {
                throw new RuntimeException("User can only cancel pending orders");
            }
            if (!order.getUser().getId().equals(actorId)) {
                throw new PermissionDeniedException("User not allowed to update this order");
            }
            // Cancel all items for user
            order.getOrderItems().forEach(item -> item.setStatus(OrderStatus.CANCELLED));
        }

        // Seller rules
        if ("ROLE_BUSINESS_OWNER".equals(actorRole.getRoleName())) {
            boolean ownsItem = order.getOrderItems().stream()
                    .anyMatch(item -> item.getProduct().getBusiness().getOwner().getId().equals(actorId));
            if (!ownsItem) {
                throw new PermissionDeniedException("Seller not allowed to update this order");
            }
            if (currentStatus == OrderStatus.CANCELLED) {
                throw new RuntimeException("Seller cannot update a cancelled order");
            }
            if (newStatus == OrderStatus.CANCELLED) {
                throw new PermissionDeniedException("Seller cannot cancel orders");
            }

            // For mixed states, enforced status flow
            OrderStatus sellerItemStatus = order.getOrderItems().stream()
                    .filter(item -> item.getProduct().getBusiness().getOwner().getId().equals(actorId))
                    .findFirst()
                    .orElseThrow()
                    .getStatus();

            if (sellerItemStatus == OrderStatus.PENDING && newStatus != OrderStatus.CONFIRMED) {
                throw new RuntimeException("Seller must CONFIRM before shipping or delivering");
            }

            if (sellerItemStatus == OrderStatus.CONFIRMED && newStatus != OrderStatus.SHIPPED) {
                throw new RuntimeException("Seller must SHIP before delivering");
            }

            if (sellerItemStatus == OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
                throw new RuntimeException("Seller can only DELIVER after shipping");
            }

            if (sellerItemStatus == OrderStatus.DELIVERED) {
                throw new RuntimeException("Delivered items cannot be updated");
            }

            if (sellerItemStatus == OrderStatus.CANCELLED) {
                throw new RuntimeException("Cancelled items cannot be updated");
            }

            // Update all seller items together
            order.getOrderItems().stream()
                    .filter(item -> item.getProduct().getBusiness().getOwner().getId().equals(actorId))
                    .forEach(item -> item.setStatus(newStatus));
        }

        // Apply update
        order.setUpdatedUser(actorId);

        // Derive overall order status from items for user
        OrderStatus overallStatus = deriveOverallStatus(order);
        order.setStatus(overallStatus);

        orderRepository.save(order);

        // Notify user if order is confirmed
        if (newStatus == OrderStatus.CONFIRMED) {
            notificationService.sendNotification(
                    order.getUser(),
                    "Your order #" + order.getId() + " has been confirmed.",
                    order
            );
        }

        // Log the change
        OrderStatusLog log = new OrderStatusLog();
        log.setOrder(order);
        log.setStatus(newStatus);
        log.setChangedBy(userRepository.findById(actorId).orElseThrow());
        log.setRole(actorRole);
        orderStatusLogRepository.save(log);

        OrderDetailsDTO dto = toOrderDetailsDTO(order);

        if ("ROLE_BUSINESS_OWNER".equals(actorRole.getRoleName())) {
            dto.setStatus(null);
            List<OrderItemDTO> sellerItems = dto.getOrderItems().stream()
                    .filter(item -> item.getOwnerId().equals(actorId))
                    .collect(Collectors.toList());
            dto.setOrderItems(sellerItems);

            sellerItems.stream()
                    .findFirst()
                    .ifPresent(item -> dto.setSellerStatus(item.getStatus()));
        }

        return dto;
    }

    // Mappers
    private OrderSummaryDTO toOrderSummaryDTO(Order order) {
        OrderSummaryDTO dto = new OrderSummaryDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setSellerStatus(null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUsername(order.getUser().getUsername());
        return dto;
    }

    private OrderDetailsDTO toOrderDetailsDTO(Order order) {
        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemDTO> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO i = new OrderItemDTO();
                    i.setProductName(item.getProduct().getTitle());
                    i.setQuantity(item.getQuantity());
                    i.setPrice(item.getPrice());
                    i.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    i.setBusinessId(item.getProduct().getBusiness().getId());
                    i.setOwnerId(item.getProduct().getBusiness().getOwner().getId());
                    i.setStatus(item.getStatus());
                    return i;
                })
                .toList();

        dto.setOrderItems(itemDtos);
        return dto;
    }

    // Helper
    private OrderStatus deriveOverallStatus(Order order) {
        boolean allPending = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.PENDING);

        boolean allConfirmed = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.CONFIRMED);

        boolean allShipped = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.SHIPPED);

        boolean allDelivered = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.DELIVERED);

        boolean allCancelled = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.CANCELLED);

        if (allCancelled) {
            return OrderStatus.CANCELLED;
        } else if (allDelivered) {
            return OrderStatus.DELIVERED;
        } else if (allShipped) {
            return OrderStatus.SHIPPED;
        } else if (allConfirmed) {
            return OrderStatus.CONFIRMED;
        } else if (allPending) {
            return OrderStatus.PENDING;
        } else {
            return OrderStatus.PROCESSING; // mixed states
        }
    }
}






