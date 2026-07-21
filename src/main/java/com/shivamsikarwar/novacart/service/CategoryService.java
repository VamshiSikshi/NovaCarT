package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.CategoryRequestDto;
import com.shivamsikarwar.novacart.dto.CategoryResponseDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }

    public CategoryResponseDto createCategory(CategoryRequestDto request){

        Category category=new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        Category savedCategory=categoryRepository.save(category);

        CategoryResponseDto response=new CategoryResponseDto();
        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());
        response.setDescription(savedCategory.getDescription());

        return response;
    }
}
