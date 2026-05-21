package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Cart.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * Busca el carrito de un usuario.
     * @param userId el ID del usuario
     * @return Optional con el carrito si existe
     */
    Optional<Cart> findByUserId(Long userId);
    
    /**
     * Busca el carrito de una sesión (guest cart).
     * @param sessionId el ID de la sesión
     * @return Optional con el carrito si existe
     */
    Optional<Cart> findBySessionId(String sessionId);
}

