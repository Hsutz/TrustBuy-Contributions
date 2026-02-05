package com.bib.TrustBuy.system.bl.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer cartId;
    private Integer userId;
    private List<CartItemDTO> items;
    private int totalQuantity;
    private BigDecimal totalPrice;
}

