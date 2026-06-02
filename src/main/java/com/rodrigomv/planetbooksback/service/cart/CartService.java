package com.rodrigomv.planetbooksback.service.cart;

import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.CartDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO getCartBySessionId(String sessionId);
    CartDTO addToCart(AddToCartRequestDTO requestDTO, Long userId, String sessionId);
    CartDTO updateCartItemQuantity(Long cartItemId, Integer quantity, Long userId, String sessionId);
    void removeCartItem(Long cartItemId, Long userId, String sessionId);
    CartDTO emptyCart(Long userId, String sessionId);
}
