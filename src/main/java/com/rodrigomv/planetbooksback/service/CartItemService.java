package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.CartItemDTO;
import com.rodrigomv.planetbooksback.model.entity.CartItem;
import com.rodrigomv.planetbooksback.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    public CartItemDTO getById(Long id) {
        CartItem it = cartItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("CartItem no encontrado"));
        return CartItemDTO.builder()
            .id(it.getId())
            .productId(it.getProductId())
            .productTitle(it.getProductTitle())
            .productImage(it.getProductImage())
            .itemKey(it.getItemKey())
            .itemTitle(it.getItemTitle())
            .itemDescription(it.getItemDescription())
            .quantity(it.getQuantity())
            .itemPrice(it.getItemPrice())
            .subtotal(it.getItemPrice().multiply(java.math.BigDecimal.valueOf(it.getQuantity())))
            .build();
    }

    @Transactional
    public void delete(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new IllegalArgumentException("CartItem no encontrado");
        }
        cartItemRepository.deleteById(id);
    }
}
