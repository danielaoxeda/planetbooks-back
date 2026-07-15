package com.rodrigomv.planetbooksback.service.product;

import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts_shouldReturnDtos() {
        Product p = Product.builder().id(1L).title("T").build();
        when(productRepository.findAll()).thenReturn(List.of(p));

        var list = productService.getAllProducts();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void decreaseStock_shouldReduceStock() {
        ProductItem item = ProductItem.builder().id(2L).stock(10).build();
        when(productItemRepository.findByProductIdAndKey(1L, "k")).thenReturn(Optional.of(item));

        productService.decreaseStock(1L, "k", 3);

        verify(productItemRepository).save(item);
        assertThat(item.getStock()).isEqualTo(7);
    }

    @Test
    void decreaseStock_shouldThrowOnInvalidQuantity() {
        assertThrows(BadRequestException.class, () -> productService.decreaseStock(1L, "k", 0));
    }

    @Test
    void decreaseStock_shouldThrowWhenNotFound() {
        when(productItemRepository.findByProductIdAndKey(1L, "k")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.decreaseStock(1L, "k", 1));
    }
}
