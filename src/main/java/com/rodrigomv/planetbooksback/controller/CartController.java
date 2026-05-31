package com.rodrigomv.planetbooksback.controller;

import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.CartDTO;
import com.rodrigomv.planetbooksback.model.dto.CartItemDTO;
import com.rodrigomv.planetbooksback.model.dto.UpdateCartQuantityRequestDTO;
import com.rodrigomv.planetbooksback.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getByUserId(userId));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDTO> addItem(@PathVariable Long cartId, @RequestBody AddToCartRequestDTO req) {
        return ResponseEntity.ok(cartService.addToCart(cartId, req));
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartItemDTO> updateItem(@PathVariable Long cartId, @PathVariable Long itemId,
                                                  @RequestBody UpdateCartQuantityRequestDTO req) {
        return ResponseEntity.ok(cartService.updateItemQuantity(cartId, itemId, req));
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartId, @PathVariable Long itemId) {
        cartService.removeItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
