package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de OrderItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productTitle;
    private String productImage;
    private String itemKey;
    private String itemTitle;
    private String itemDescription;
    private BigDecimal itemPrice;
    private Integer quantity;
    private BigDecimal subtotal; // quantity * itemPrice
}

