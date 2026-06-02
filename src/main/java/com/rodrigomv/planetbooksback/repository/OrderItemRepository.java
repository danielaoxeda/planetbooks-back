package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad OrderItem.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Elimina todos los items de una orden.
     * @param orderId el ID de la orden
     */
    void deleteByOrderId(Long orderId);
}

