package com.rodrigomv.planetbooksback.service.cart;

import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.entity.*;
import com.rodrigomv.planetbooksback.repository.CartItemRepository;
import com.rodrigomv.planetbooksback.repository.CartRepository;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTests {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addToCart_shouldAddNewItem() {
        Product product = Product.builder().id(1L).title("P").build();
        ProductItem productItem = ProductItem.builder()
            .id(2L)
            .product(product)
            .key("k")
            .title("t")
            .price(new BigDecimal("10.00"))
            .stock(5)
            .image("img")
            .description("d")
            .build();

        Cart cart = Cart.builder().id(3L).items(new ArrayList<>()).build();

        AddToCartRequestDTO req = AddToCartRequestDTO.builder().productId(1L).itemKey("k").quantity(2).build();

        when(productItemRepository.findByProductIdAndKey(1L, "k")).thenReturn(Optional.of(productItem));
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

        var dto = cartService.addToCart(req, 10L, null);

        assertThat(dto.getItems()).hasSize(1);
        verify(cartRepository).save(cart);
    }

    @Test
    void addToCart_shouldThrowOnInvalidQuantity() {
        assertThrows(BadRequestException.class, () -> cartService.addToCart(null, 1L, null));
        AddToCartRequestDTO req = AddToCartRequestDTO.builder().productId(1L).itemKey("k").quantity(0).build();
        assertThrows(BadRequestException.class, () -> cartService.addToCart(req, 1L, null));
    }

    @Test
    void updateCartItemQuantity_shouldThrowWhenNotFound() {
        // Prepare a cart so resolveCart doesn't fail earlier
        Cart cart = Cart.builder().id(10L).items(new ArrayList<>()).build();
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCartItemQuantity(99L, 1, 1L, null));
    }
}
