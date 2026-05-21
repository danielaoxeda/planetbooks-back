package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.ProductDTO;
import com.rodrigomv.planetbooksback.model.dto.ProductItemDTO;
import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de productos.
 * Ejemplo de implementación de lógica de negocio.
 *
 * Funcionalidades:
 * - Búsqueda de productos
 * - Filtrado por tag, level, categorías
 * - Obtención de productos con paginación
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Obtiene todos los productos con paginación.
     *
     * @param pageable parámetros de paginación
     * @return página de productos
     */
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Obteniendo productos con paginación");

        return productRepository.findAll(pageable)
            .map(this::convertToDTO);
    }

    /**
     * Busca productos por título.
     *
     * @param title título a buscar
     * @param pageable parámetros de paginación
     * @return página de productos coincidentes
     */
    public Page<ProductDTO> searchByTitle(String title, Pageable pageable) {
        log.debug("Buscando productos por título: {}", title);

        return productRepository.findByTitleContainingIgnoreCase(title, pageable)
            .map(this::convertToDTO);
    }

    /**
     * Obtiene productos por tag.
     *
     * @param tag tag a filtrar
     * @param pageable parámetros de paginación
     * @return página de productos con el tag
     */
    public Page<ProductDTO> getProductsByTag(String tag, Pageable pageable) {
        log.debug("Obteniendo productos con tag: {}", tag);

        return productRepository.findByTag(tag, pageable)
            .map(this::convertToDTO);
    }

    /**
     * Obtiene productos por level.
     *
     * @param level nivel a filtrar
     * @param pageable parámetros de paginación
     * @return página de productos con el nivel
     */
    public Page<ProductDTO> getProductsByLevel(String level, Pageable pageable) {
        log.debug("Obteniendo productos con level: {}", level);

        return productRepository.findByLevel(level, pageable)
            .map(this::convertToDTO);
    }

    /**
     * Obtiene un producto por ID.
     *
     * @param id ID del producto
     * @return DTO del producto
     */
    public ProductDTO getProductById(Long id) {
        log.debug("Obteniendo producto con ID: {}", id);

        return productRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> {
                log.warn("Producto no encontrado: {}", id);
                return new IllegalArgumentException("Producto no encontrado");
            });
    }

    /**
     * Obtiene los tags únicos de los productos.
     * Nota: Esta es una operación de demostración. En producción,
     * considera cachear este resultado.
     *
     * @return lista de tags únicos
     */
    public List<String> getAllTags() {
        log.debug("Obteniendo todos los tags");

        return productRepository.findAll()
            .stream()
            .map(Product::getTag)
            .distinct()
            .filter(tag -> tag != null && !tag.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Obtiene los niveles únicos de los productos.
     * Nota: Esta es una operación de demostración. En producción,
     * considera cachear este resultado.
     *
     * @return lista de niveles únicos
     */
    public List<String> getAllLevels() {
        log.debug("Obteniendo todos los niveles");

        return productRepository.findAll()
            .stream()
            .map(Product::getLevel)
            .distinct()
            .filter(level -> level != null && !level.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Product a DTO.
     *
     * @param product entidad producto
     * @return DTO producto
     */
    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
            .id(product.getId())
            .title(product.getTitle())
            .description(product.getDescription())
            .tag(product.getTag())
            .categories(product.getCategories())
            .level(product.getLevel())
            .image(product.getImage())
            .gallery(product.getGallery())
            .pages(product.getPages())
            .format(product.getFormat())
            .publisher(product.getPublisher())
            .language(product.getLanguage())
            .items(product.getItems() != null ?
                product.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList()) :
                List.of())
            .build();
    }

    /**
     * Convierte una entidad ProductItem a DTO.
     *
     * @param item entidad item
     * @return DTO item
     */
    private ProductItemDTO convertItemToDTO(ProductItem item) {
        return ProductItemDTO.builder()
            .id(item.getId())
            .key(item.getKey())
            .title(item.getTitle())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .pages(item.getPages())
            .format(item.getFormat())
            .isDefault(item.getIsDefault())
            .build();
    }
}

