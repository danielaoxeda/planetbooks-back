package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad ProductItem.
 */
@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
    
    /**
     * Busca un ProductItem por su producto ID y key.
     * @param productId el ID del producto
     * @param key el key único del item
     * @return Optional con el ProductItem si existe
     */
    Optional<ProductItem> findByProductIdAndKey(Long productId, String key);
}

