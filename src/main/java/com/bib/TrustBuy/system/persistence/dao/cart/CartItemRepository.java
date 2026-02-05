package com.bib.TrustBuy.system.persistence.dao.cart;

import com.bib.TrustBuy.system.persistence.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByIdAndDelFlgFalse(Integer itemId);
}
