package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO para respuesta de Order con sus items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private Instant createdAt;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
}

