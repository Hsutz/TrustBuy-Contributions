package com.bib.TrustBuy.system.bl.dto.order;

import com.bib.TrustBuy.system.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDetailsDTO {
    private Integer id;

    private OrderStatus status;
    private OrderStatus sellerStatus;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemDTO> orderItems;
}
