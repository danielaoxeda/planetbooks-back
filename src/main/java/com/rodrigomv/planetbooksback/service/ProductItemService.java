package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.ProductItemDTO;
import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;

    public ProductItemDTO getById(Long id) {
        ProductItem item = productItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ProductItem no encontrado"));
        return convertToDTO(item);
    }

    public List<ProductItemDTO> getByProductId(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        return product.getItems().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProductItemDTO create(Long productId, ProductItemDTO dto) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        ProductItem item = ProductItem.builder()
            .key(dto.getKey())
            .title(dto.getTitle())
            .price(dto.getPrice())
            .image(dto.getImage())
            .description(dto.getDescription())
            .pages(dto.getPages())
            .format(dto.getFormat())
            .isDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false)
            .product(product)
            .build();

        product.getItems().add(item);
        ProductItem saved = productItemRepository.save(item);
        return convertToDTO(saved);
    }

    @Transactional
    public ProductItemDTO update(Long id, ProductItemDTO dto) {
        ProductItem item = productItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ProductItem no encontrado"));

        item.setKey(dto.getKey());
        item.setTitle(dto.getTitle());
        item.setPrice(dto.getPrice());
        item.setImage(dto.getImage());
        item.setDescription(dto.getDescription());
        item.setPages(dto.getPages());
        item.setFormat(dto.getFormat());
        item.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : item.getIsDefault());

        ProductItem saved = productItemRepository.save(item);
        return convertToDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!productItemRepository.existsById(id)) {
            throw new IllegalArgumentException("ProductItem no encontrado");
        }
        productItemRepository.deleteById(id);
    }

    private ProductItemDTO convertToDTO(ProductItem item) {
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
