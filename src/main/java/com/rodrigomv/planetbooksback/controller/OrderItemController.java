package com.rodrigomv.planetbooksback.controller;

import com.rodrigomv.planetbooksback.model.dto.OrderItemDTO;
import com.rodrigomv.planetbooksback.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
