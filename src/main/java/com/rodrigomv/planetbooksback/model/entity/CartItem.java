package com.rodrigomv.planetbooksback.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad que representa un item en el carrito.
 * 
 * Almacena un snapshot de los datos del producto en el momento de añadirse al carrito.
 * Esto asegura que si el producto cambia, el carrito mantiene los datos históricos.
 * 
 * Campos snapshot:
 * - productId, productTitle, productImage
 * - itemKey, itemTitle, itemDescription
 * - itemPrice
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Referencia al carrito que contiene este item.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    /**
     * Referencia opcional al ProductItem actual (puede ser null si fue eliminado).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_item_id")
    private ProductItem productItem;
    
    // --- SNAPSHOT de datos del producto ---
    @NotNull
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String productTitle;
    
    private String productImage;
    
    @NotNull
    @Column(nullable = false)
    private String itemKey;
    
    @Column(nullable = false)
    private String itemTitle;
    
    @Column(columnDefinition = "TEXT")
    private String itemDescription;
    
    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal itemPrice;
}

