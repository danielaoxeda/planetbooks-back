package com.rodrigomv.planetbooksback.controller.order;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.model.dto.SalesSummaryDTO;
import com.rodrigomv.planetbooksback.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderDTO> createOrder(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.createOrder(userId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<OrderDTO>> getMyOrders(@RequestParam Long userId, Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderDTO> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> listAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalesSummaryDTO> getSalesSummary() {
        return ResponseEntity.ok(orderService.exportSalesSummary());
    }
}
