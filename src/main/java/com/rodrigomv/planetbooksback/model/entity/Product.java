package com.rodrigomv.planetbooksback.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto (libro) en el catálogo.
 * 
 * Características:
 * - Puede tener múltiples ProductItem (variaciones con diferentes precios)
 * - Categories y gallery se almacenan como colecciones de strings
 * - Incluye metadatos: pages, format, publisher, language, etc.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_tag", columnList = "tag"),
    @Index(name = "idx_product_level", columnList = "level")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String tag;
    
    private String level;
    
    private String image;
    
    private String pages;
    
    private String format;
    
    private String publisher;
    
    private String language;
    
    /**
     * Colección de categorías como strings.
     * Se persiste en tabla product_categories.
     */
    @ElementCollection
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category")
    @Builder.Default
    private List<String> categories = new ArrayList<>();
    
    /**
     * Colección de URLs de galería.
     * Se persiste en tabla product_gallery.
     */
    @ElementCollection
    @CollectionTable(name = "product_gallery", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "gallery_url")
    @Builder.Default
    private List<String> gallery = new ArrayList<>();
    
    /**
     * Items del producto (variaciones con diferentes precios/formatos).
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductItem> items = new ArrayList<>();
}

