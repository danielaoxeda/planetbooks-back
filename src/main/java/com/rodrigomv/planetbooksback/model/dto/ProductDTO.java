package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de Product con sus items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private List<String> categories;
    private String level;
    private String image;
    private List<String> gallery;
    private String pages;
    private String format;
    private String publisher;
    private String language;
    private List<ProductItemDTO> items;
}

