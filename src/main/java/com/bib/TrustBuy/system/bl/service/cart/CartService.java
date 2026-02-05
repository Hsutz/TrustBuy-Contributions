package com.bib.TrustBuy.system.bl.service.cart;

import com.bib.TrustBuy.system.bl.dto.cart.CartDTO;

public interface CartService {
    void addItemToCart(Integer userId, Integer productId, int quantity);

    CartDTO getCartByUser(Integer userId);

    void updateCartItem(Integer userId, Integer itemId, int quantity, Boolean selected);

    void removeCartItem(Integer userId, Integer itemId);

    void clearCart(Integer userId);
}