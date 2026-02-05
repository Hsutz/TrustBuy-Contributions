package com.bib.TrustBuy.system.web.controller.cart;

import com.bib.TrustBuy.system.bl.dto.cart.CartDTO;
import com.bib.TrustBuy.system.bl.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. Add product to cart
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam int quantity) {
        cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // 2. View cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // 3. Update cart item
    @PostMapping("/update-item/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @RequestParam Integer userId,
            @PathVariable Integer itemId,
            @RequestParam int quantity,
            @RequestParam(required = false) Boolean selected) {
        boolean isSelected = Boolean.TRUE.equals(selected);
        cartService.updateCartItem(userId, itemId, quantity, isSelected);
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // 4. Remove cart item
    @PostMapping("/remove")
    public ResponseEntity<CartDTO> removeCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer itemId) {
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // 5. Clear cart
    @PostMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(@RequestParam Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }
}