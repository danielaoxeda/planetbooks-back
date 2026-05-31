package com.rodrigomv.planetbooksback.controller;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
