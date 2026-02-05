package com.bib.TrustBuy.system.bl.service.cart.impl;

import com.bib.TrustBuy.system.bl.dto.cart.CartDTO;
import com.bib.TrustBuy.system.bl.dto.cart.CartItemDTO;
import com.bib.TrustBuy.system.bl.service.cart.CartService;
import com.bib.TrustBuy.system.persistence.dao.cart.CartItemRepository;
import com.bib.TrustBuy.system.persistence.dao.cart.CartRepository;
import com.bib.TrustBuy.system.persistence.dao.product.ProductRepo;
import com.bib.TrustBuy.system.persistence.dao.user.UserRepository;
import com.bib.TrustBuy.system.persistence.entity.User;
import com.bib.TrustBuy.system.persistence.entity.cart.Cart;
import com.bib.TrustBuy.system.persistence.entity.cart.CartItem;
import com.bib.TrustBuy.system.persistence.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepo productRepository;
    private final UserRepository userRepository;

    // 1. Add item to cart
    @Override
    public void addItemToCart(Integer userId, Integer productId, int quantity) {
        // Quantity check
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Prevent adding out-of-stock items
        if (product.getStock() <= 0) {
            throw new RuntimeException("Product is out of stock");
        }

        // Prevent adding more than available
        if (quantity > product.getStock()) {
            throw new RuntimeException("Only " + product.getStock() + " items left in stock");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId) && !item.isDelFlg())
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (newQuantity > product.getStock()) {
                throw new RuntimeException("Cannot add more than available stock (" + product.getStock() + ")");
            }

            item.setQuantity(newQuantity);
            item.setUpdatedUser(userId);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            newItem.setCreatedUser(userId);
            newItem.setUpdatedUser(userId);
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    // 2. View cart
    @Override
    public CartDTO getCartByUser(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .filter(item -> !item.isDelFlg())
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getSelected(),
                        item.getProduct().getPrice()
                ))
                .toList();

        // Filter only selected items for totals
        List<CartItemDTO> selectedItems = itemDTOs.stream()
                .filter(CartItemDTO::getSelected)
                .toList();

        int totalQuantity = selectedItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();

        BigDecimal totalPrice = selectedItems.stream()
                .map(i -> BigDecimal.valueOf(i.getQuantity()).multiply(i.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDTO(
                cart.getId(),
                cart.getUser().getId(),
                itemDTOs,
                totalQuantity,
                totalPrice
        );
    }

    // 3. Update cart item
    public void updateCartItem(Integer userId, Integer itemId, int quantity, Boolean selected) {
        // Quantity check
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = cartItemRepository.findByIdAndDelFlgFalse(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        item.setSelected(selected);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedUser(userId);
        cartItemRepository.save(item);
    }

    // 4. Remove cart item
    @Override
    public void removeCartItem(Integer userId, Integer itemId) {
        CartItem item = cartItemRepository.findByIdAndDelFlgFalse(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setDelFlg(true);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedUser(userId);
        cartItemRepository.save(item);
    }

    // 5. Clear cart
    @Override
    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().forEach(item -> {
            item.setDelFlg(true);
            item.setUpdatedAt(LocalDateTime.now());
            item.setUpdatedUser(userId);
        });

        cart.setUpdatedAt(LocalDateTime.now());
        cart.setUpdatedUser(userId);
        cartRepository.save(cart);
    }

}