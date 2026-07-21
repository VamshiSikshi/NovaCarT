package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.CategoryRequestDto;
import com.shivamsikarwar.novacart.dto.CategoryResponseDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService=categoryService;
    }

    @PostMapping
    public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto request){
        return categoryService.createCategory(request);
    }
}
