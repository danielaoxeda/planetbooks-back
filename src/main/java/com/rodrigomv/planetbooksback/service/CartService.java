package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.CartDTO;
import com.rodrigomv.planetbooksback.model.dto.CartItemDTO;
import com.rodrigomv.planetbooksback.model.dto.UpdateCartQuantityRequestDTO;
import com.rodrigomv.planetbooksback.model.entity.Cart;
import com.rodrigomv.planetbooksback.model.entity.CartItem;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.repository.CartItemRepository;
import com.rodrigomv.planetbooksback.repository.CartRepository;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductItemRepository productItemRepository;

    public CartDTO getById(Long id) {
        Cart cart = cartRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cart no encontrado"));
        return convertToDTO(cart);
    }

    public CartDTO getByUserId(Long userId) {
        return cartRepository.findByUserId(userId).map(this::convertToDTO)
            .orElseThrow(() -> new IllegalArgumentException("Cart no encontrado para usuario"));
    }

    public CartDTO createCart(Cart cart) {
        Cart saved = cartRepository.save(cart);
        return convertToDTO(saved);
    }

    @Transactional
    public CartItemDTO addToCart(Long cartId, AddToCartRequestDTO req) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("Cart no encontrado"));

        ProductItem item = productItemRepository.findByProductIdAndKey(req.getProductId(), req.getItemKey())
            .orElseThrow(() -> new IllegalArgumentException("ProductItem no encontrado"));

        // build snapshot
        CartItem cartItem = CartItem.builder()
            .cart(cart)
            .productItem(item)
            .productId(item.getProduct().getId())
            .productTitle(item.getProduct().getTitle())
            .productImage(item.getImage())
            .itemKey(item.getKey())
            .itemTitle(item.getTitle())
            .itemDescription(item.getDescription())
            .quantity(req.getQuantity() != null ? req.getQuantity() : 1)
            .itemPrice(item.getPrice())
            .build();

        cart.getItems().add(cartItem);
        CartItem saved = cartItemRepository.save(cartItem);
        return convertItemToDTO(saved);
    }

    @Transactional
    public CartItemDTO updateItemQuantity(Long cartId, Long itemId, UpdateCartQuantityRequestDTO req) {
        CartItem it = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("CartItem no encontrado"));

        if (!it.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("CartItem no pertenece al carrito especificado");
        }

        it.setQuantity(req.getQuantity());
        CartItem saved = cartItemRepository.save(it);
        return convertItemToDTO(saved);
    }

    @Transactional
    public void removeItem(Long cartId, Long itemId) {
        CartItem it = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("CartItem no encontrado"));
        if (!it.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("CartItem no pertenece al carrito especificado");
        }
        cartItemRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("Cart no encontrado"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new IllegalArgumentException("Cart no encontrado");
        }
        cartRepository.deleteById(cartId);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList());
        BigDecimal total = items.stream().map(i -> i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
            .id(cart.getId())
            .createdAt(cart.getCreatedAt())
            .items(items)
            .totalAmount(total)
            .build();
    }

    private CartItemDTO convertItemToDTO(CartItem it) {
        return CartItemDTO.builder()
            .id(it.getId())
            .productId(it.getProductId())
            .productTitle(it.getProductTitle())
            .productImage(it.getProductImage())
            .itemKey(it.getItemKey())
            .itemTitle(it.getItemTitle())
            .itemDescription(it.getItemDescription())
            .quantity(it.getQuantity())
            .itemPrice(it.getItemPrice())
            .subtotal(it.getItemPrice().multiply(java.math.BigDecimal.valueOf(it.getQuantity())))
            .build();
    }
}
