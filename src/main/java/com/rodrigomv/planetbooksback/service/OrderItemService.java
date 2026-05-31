package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.OrderItemDTO;
import com.rodrigomv.planetbooksback.model.entity.OrderItem;
import com.rodrigomv.planetbooksback.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemDTO getById(Long id) {
        OrderItem it = orderItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OrderItem no encontrado"));
        return OrderItemDTO.builder()
            .id(it.getId())
            .productId(it.getProductId())
            .productTitle(it.getProductTitle())
            .productImage(it.getProductImage())
            .itemKey(it.getItemKey())
            .itemTitle(it.getItemTitle())
            .itemDescription(it.getItemDescription())
            .itemPrice(it.getItemPrice())
            .quantity(it.getQuantity())
            .subtotal(it.getItemPrice().multiply(java.math.BigDecimal.valueOf(it.getQuantity())))
            .build();
    }

    @Transactional
    public void delete(Long id) {
        if (!orderItemRepository.existsById(id)) {
            throw new IllegalArgumentException("OrderItem no encontrado");
        }
        orderItemRepository.deleteById(id);
    }
}
