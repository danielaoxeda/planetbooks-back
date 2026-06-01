package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de CartItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private String itemKey;
    private String itemTitle;
    private String itemDescription;
    private Integer quantity;
    private BigDecimal itemPrice;
    private BigDecimal subtotal; // quantity * itemPrice (calculado en servicio)
}

