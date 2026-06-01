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
 * Entidad que representa un item en una orden.
 *
 * Es un snapshot de los datos del CartItem/ProductItem en el momento de crear la orden.
 * Almacena toda la información necesaria para mantener un registro histórico
 * sin dependencias de cambios posteriores en Product o ProductItem.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia a la orden que contiene este item.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

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
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal itemPrice;

    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer quantity;
}

