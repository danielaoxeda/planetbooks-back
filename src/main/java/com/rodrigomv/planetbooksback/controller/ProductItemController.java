package com.rodrigomv.planetbooksback.controller;

import com.rodrigomv.planetbooksback.model.dto.ProductItemDTO;
import com.rodrigomv.planetbooksback.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-items")
@RequiredArgsConstructor
public class ProductItemController {

    private final ProductItemService productItemService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductItemDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productItemService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductItemDTO>> listByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productItemService.getByProductId(productId));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductItemDTO> create(@PathVariable Long productId, @RequestBody ProductItemDTO dto) {
        return ResponseEntity.ok(productItemService.create(productId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductItemDTO> update(@PathVariable Long id, @RequestBody ProductItemDTO dto) {
        return ResponseEntity.ok(productItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
