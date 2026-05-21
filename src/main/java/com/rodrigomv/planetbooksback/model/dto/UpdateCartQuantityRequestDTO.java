package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para request de actualizar cantidad en carrito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartQuantityRequestDTO {
    private Integer quantity;
}

