package com.rodrigomv.planetbooksback.service.product;

import com.rodrigomv.planetbooksback.model.dto.ProductDTO;
import com.rodrigomv.planetbooksback.model.dto.ProductItemDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> searchProducts(String title, String tag, String level);
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    ProductDTO patchProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);

    ProductItemDTO getProductItemById(Long id);
    List<ProductItemDTO> getAllProductItems();
    ProductItemDTO createProductItem(ProductItemDTO productItemDTO);
    ProductItemDTO updateProductItem(Long id, ProductItemDTO productItemDTO);
    ProductItemDTO patchProductItem(Long id, ProductItemDTO productItemDTO);
    void deleteProductItem(Long id);
    void decreaseStock(Long productId, String itemKey, Integer quantity);

    List<String> getAllTags();
    List<String> getAllLevels();
}
