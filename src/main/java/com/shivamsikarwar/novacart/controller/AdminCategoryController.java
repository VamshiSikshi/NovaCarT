package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.CategoryRequestDto;
import com.shivamsikarwar.novacart.dto.CategoryResponseDto;
import com.shivamsikarwar.novacart.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    // =========================
    // GET ALL CATEGORIES
    // =========================

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    // =========================
    // GET CATEGORY BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }

    // =========================
    // CREATE CATEGORY
    // =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(
            @Valid @RequestBody CategoryRequestDto request) {

        return categoryService.createCategory(request);
    }

    // =========================
    // UPDATE CATEGORY
    // =========================

    @PutMapping("/{id}")
    public CategoryResponseDto updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto request) {

        return categoryService.updateCategory(
                id,
                request
        );
    }

    // =========================
    // DELETE CATEGORY
    // =========================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);
    }
}