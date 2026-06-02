package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad CartItem.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Elimina todos los items de un carrito.
     * @param cartId el ID del carrito
     */
    void deleteByCartId(Long cartId);
}

