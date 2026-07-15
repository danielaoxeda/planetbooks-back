package com.rodrigomv.planetbooksback.service.order;

import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.entity.*;
import com.rodrigomv.planetbooksback.repository.CartRepository;
import com.rodrigomv.planetbooksback.repository.OrderRepository;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
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
class OrderServiceImplTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductItemRepository productItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_shouldCreateAndClearCart() {
        User user = User.builder().id(1L).build();
        CartItem cartItem = CartItem.builder()
            .productId(11L)
            .itemKey("k")
            .quantity(2)
            .itemPrice(new BigDecimal("5.00"))
            .productTitle("t").productImage("i").itemTitle("it").itemDescription("d")
            .build();
                java.util.ArrayList<CartItem> items = new ArrayList<>(List.of(cartItem));
                Cart cart = Cart.builder().id(5L).user(user).items(items).build();
                cartItem.setCart(cart);

        ProductItem productItem = ProductItem.builder().id(22L).stock(5).price(new BigDecimal("5.00")).build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productItemRepository.findByProductIdAndKey(11L, "k")).thenReturn(Optional.of(productItem));

        Order saved = Order.builder().id(100L).user(user).build();
        when(orderRepository.save(any())).thenReturn(saved);

        var dto = orderService.createOrder(1L);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(cart.getItems()).isEmpty();
        verify(productItemRepository).save(productItem);
        verify(cartRepository).save(cart);
    }

    @Test
    void createOrder_shouldThrowWhenCartEmpty() {
        Cart cart = Cart.builder().id(2L).items(new ArrayList<>()).build();
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));
        assertThrows(BadRequestException.class, () -> orderService.createOrder(2L));
    }

    @Test
    void createOrder_shouldThrowWhenCartMissing() {
        when(cartRepository.findByUserId(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(3L));
    }
}
