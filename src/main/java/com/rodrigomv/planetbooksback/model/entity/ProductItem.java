package com.rodrigomv.planetbooksback.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad que representa una variación de producto.
 * 
 * Cada ProductItem pertenece a un Product y tiene:
 * - Key único (identificador dentro del producto)
 * - Precio individual
 * - Stock disponible
 * - Puede ser marcado como item por defecto
 * 
 * Constraints:
 * - (product_id, item_key) debe ser UNIQUE
 * - Precio con precision 12, scale 2
 */
@Entity
@Table(name = "product_items", uniqueConstraints = {
    @UniqueConstraint(name = "uk_product_item_key", columnNames = {"product_id", "item_key"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String key;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    private String image;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String pages;
    
    private String format;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;
    
    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;
    
    /**
     * Referencia al producto padre.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

