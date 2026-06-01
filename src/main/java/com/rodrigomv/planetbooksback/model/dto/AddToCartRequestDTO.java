package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para request de añadir item al carrito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequestDTO {
    private Long productId;
    private String itemKey;
    private Integer quantity;
}

