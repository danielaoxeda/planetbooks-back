package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Busca productos por tag.
     * @param tag el tag a filtrar
     * @return lista de productos con el tag
     */
    List<Product> findByTag(String tag);
    
    /**
     * Busca productos por level.
     * @param level el nivel a filtrar
     * @return lista de productos con el nivel
     */
    List<Product> findByLevel(String level);
    
    /**
     * Busca productos por title.
     * @param title parte del título a buscar
     * @return lista de productos
     */
    List<Product> findByTitleContainingIgnoreCase(String title);
}

