package com.rodrigomv.planetbooksback.repository;

import com.rodrigomv.planetbooksback.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Busca productos por title con paginación.
     * @param title parte del título a buscar
     * @param pageable parámetros de paginación
     * @return página de productos
     */
    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Busca productos por tag con paginación.
     * @param tag el tag a filtrar
     * @param pageable parámetros de paginación
     * @return página de productos
     */
    Page<Product> findByTag(String tag, Pageable pageable);
    
    /**
     * Busca productos por level con paginación.
     * @param level el nivel a filtrar
     * @param pageable parámetros de paginación
     * @return página de productos
     */
    Page<Product> findByLevel(String level, Pageable pageable);
}

