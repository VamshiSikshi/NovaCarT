package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.ProductResponseDto;
import com.shivamsikarwar.novacart.dto.ProductRequestDto;
import com.shivamsikarwar.novacart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    // =========================
    // ALL PRODUCTS
    // =========================

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
        );
    }

    // =========================
    // CREATE PRODUCT
    // =========================

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody ProductRequestDto request) {

        return ResponseEntity.ok(
                productService.createProduct(request)
        );
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto request) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }

    // =========================
    // DELETE PRODUCT
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer threshold) {

        return ResponseEntity.ok(
                productService.getLowStockProducts(threshold)
        );
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {

        return ResponseEntity.ok(
                productService.updateStock(id, stock)
        );
    }
}