package com.rodrigomv.planetbooksback.service.cart;

import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.CartDTO;
import com.rodrigomv.planetbooksback.model.dto.CartItemDTO;
import com.rodrigomv.planetbooksback.model.entity.Cart;
import com.rodrigomv.planetbooksback.model.entity.CartItem;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.repository.CartItemRepository;
import com.rodrigomv.planetbooksback.repository.CartRepository;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductItemRepository productItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        return convertToDTO(getOrCreateCartForUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartBySessionId(String sessionId) {
        return convertToDTO(getOrCreateCartForSession(sessionId));
    }

    @Override
    public CartDTO addToCart(AddToCartRequestDTO requestDTO, Long userId, String sessionId) {
        if (requestDTO == null || requestDTO.getQuantity() == null || requestDTO.getQuantity() <= 0) {
            throw new BadRequestException("Cantidad inválida");
        }

        Cart cart = resolveCart(userId, sessionId);
        ProductItem productItem = productItemRepository.findByProductIdAndKey(requestDTO.getProductId(), requestDTO.getItemKey())
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(requestDTO.getProductId()) && item.getItemKey().equals(requestDTO.getItemKey()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int quantity = item.getQuantity() + requestDTO.getQuantity();
            if (productItem.getStock() < quantity) {
                throw new BadRequestException("Stock insuficiente");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        } else {
            if (productItem.getStock() < requestDTO.getQuantity()) {
                throw new BadRequestException("Stock insuficiente");
            }
            CartItem cartItem = CartItem.builder()
                .cart(cart)
                .productItem(productItem)
                .productId(requestDTO.getProductId())
                .productTitle(productItem.getProduct().getTitle())
                .productImage(productItem.getImage())
                .itemKey(productItem.getKey())
                .itemTitle(productItem.getTitle())
                .itemDescription(productItem.getDescription())
                .quantity(requestDTO.getQuantity())
                .itemPrice(productItem.getPrice())
                .build();
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Override
    public CartDTO updateCartItemQuantity(Long cartItemId, Integer quantity, Long userId, String sessionId) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Cantidad inválida");
        }

        Cart cart = resolveCart(userId, sessionId);
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem no encontrado"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("CartItem no pertenece al carrito");
        }

        ProductItem productItem = productItemRepository.findByProductIdAndKey(item.getProductId(), item.getItemKey())
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));
        if (productItem.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return convertToDTO(cart);
    }

    @Override
    public void removeCartItem(Long cartItemId, Long userId, String sessionId) {
        Cart cart = resolveCart(userId, sessionId);
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("CartItem no encontrado"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("CartItem no pertenece al carrito");
        }
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Override
    public CartDTO emptyCart(Long userId, String sessionId) {
        Cart cart = resolveCart(userId, sessionId);
        cart.getItems().clear();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    private Cart resolveCart(Long userId, String sessionId) {
        if (userId != null) {
            return getOrCreateCartForUser(userId);
        }
        if (sessionId != null && !sessionId.isBlank()) {
            return getOrCreateCartForSession(sessionId);
        }
        throw new BadRequestException("Se requiere userId o sessionId");
    }

    private Cart getOrCreateCartForUser(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private Cart getOrCreateCartForSession(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
            .orElseGet(() -> cartRepository.save(Cart.builder().sessionId(sessionId).build()));
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList());
        BigDecimal total = items.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartDTO.builder()
            .id(cart.getId())
            .createdAt(cart.getCreatedAt())
            .items(items)
            .totalAmount(total)
            .build();
    }

    private CartItemDTO convertItemToDTO(CartItem item) {
        BigDecimal subtotal = item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemDTO.builder()
            .id(item.getId())
            .cartId(item.getCart().getId())
            .productId(item.getProductId())
            .productTitle(item.getProductTitle())
            .productImage(item.getProductImage())
            .itemKey(item.getItemKey())
            .itemTitle(item.getItemTitle())
            .itemDescription(item.getItemDescription())
            .quantity(item.getQuantity())
            .itemPrice(item.getItemPrice())
            .subtotal(subtotal)
            .build();
    }
}
