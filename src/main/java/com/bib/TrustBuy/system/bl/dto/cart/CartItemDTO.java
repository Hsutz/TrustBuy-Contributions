package com.bib.TrustBuy.system.bl.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Integer itemId;
    private Integer productId;
    private String productName;
    private int quantity;
    private Boolean selected;
    private BigDecimal price;
}
