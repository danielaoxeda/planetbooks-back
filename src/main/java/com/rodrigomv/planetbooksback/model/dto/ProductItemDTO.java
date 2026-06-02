package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de ProductItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemDTO {
    private Long id;
    private Long productId;
    private String key;
    private String title;
    private BigDecimal price;
    private String image;
    private String description;
    private String pages;
    private String format;
    private Integer stock;
    private Boolean isDefault;
}

