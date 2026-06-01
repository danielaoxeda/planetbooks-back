package com.rodrigomv.planetbooksback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un carrito de compras.
 * 
 * Características:
 * - Un carrito puede pertenecer a un usuario (autenticado) o ser anónimo (sessionId)
 * - user_id puede ser nullable para carritos de invitado
 * - Los items se persisten con snapshot de datos del producto
 * - Esto evita inconsistencias si el producto cambia posteriormente
 */
@Entity
@Table(name = "carts", indexes = {
    @Index(name = "idx_cart_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Usuario propietario del carrito (nullable para carritos de invitado).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * ID de sesión para carritos de invitado (sin usuario autenticado).
     */
    private String sessionId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    /**
     * Items en el carrito.
     * Se eliminan en cascada al eliminar el carrito.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
}

