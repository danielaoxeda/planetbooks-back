package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.Order;
import com.rodrigomv.planetbooksback.model.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Order.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Busca todas las órdenes de un usuario con paginación.
     * @param userId el ID del usuario
     * @param pageable parámetros de paginación
     * @return página de órdenes
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Busca todas las órdenes de un usuario.
     * @param userId el ID del usuario
     * @return lista de órdenes
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Busca órdenes por estado.
     * @param status el estado de la orden
     * @return lista de órdenes con ese estado
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Busca órdenes por usuario y estado.
     * @param userId el ID del usuario
     * @param status el estado de la orden
     * @return lista de órdenes
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}

