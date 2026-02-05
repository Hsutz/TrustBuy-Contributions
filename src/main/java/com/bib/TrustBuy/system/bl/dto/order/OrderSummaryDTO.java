package com.bib.TrustBuy.system.bl.dto.order;

import com.bib.TrustBuy.system.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderSummaryDTO {
    private Integer id;
    private OrderStatus status; // overall status
    private OrderStatus sellerStatus;  // seller-specific status
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String username;
}