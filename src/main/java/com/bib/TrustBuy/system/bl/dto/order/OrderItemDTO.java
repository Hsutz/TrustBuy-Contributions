package com.bib.TrustBuy.system.bl.dto.order;

import com.bib.TrustBuy.system.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;
    private Integer businessId;
    private Integer ownerId;
    private OrderStatus status;
}
