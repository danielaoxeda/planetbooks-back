package com.rodrigomv.planetbooksback.controller.cart;

import com.rodrigomv.planetbooksback.model.dto.AddToCartRequestDTO;
import com.rodrigomv.planetbooksback.model.dto.CartDTO;
import com.rodrigomv.planetbooksback.model.dto.UpdateCartQuantityRequestDTO;
import com.rodrigomv.planetbooksback.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String sessionId) {
        if (userId != null) {
            return ResponseEntity.ok(cartService.getCartByUserId(userId));
        }
        return ResponseEntity.ok(cartService.getCartBySessionId(sessionId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(@RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String sessionId,
                                           @RequestBody AddToCartRequestDTO requestDTO) {
        return ResponseEntity.ok(cartService.addToCart(requestDTO, userId, sessionId));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(@PathVariable Long itemId,
                                                      @RequestParam(required = false) Long userId,
                                                      @RequestParam(required = false) String sessionId,
                                                      @RequestBody UpdateCartQuantityRequestDTO requestDTO) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(itemId, requestDTO.getQuantity(), userId, sessionId));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId,
                                           @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) String sessionId) {
        cartService.removeCartItem(itemId, userId, sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<CartDTO> emptyCart(@RequestParam(required = false) Long userId,
                                             @RequestParam(required = false) String sessionId) {
        return ResponseEntity.ok(cartService.emptyCart(userId, sessionId));
    }
}
