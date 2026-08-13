package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.CategoryRequestDto;
import com.shivamsikarwar.novacart.dto.CategoryResponseDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDto createCategory(CategoryRequestDto request){
        Category category=toEntity(request);
        Category savedCategory=categoryRepository.save(category);
        return toResponseDto(savedCategory);
    }

    public List<CategoryResponseDto> getAllCategories (){
        List<Category> categories=categoryRepository.findAll();
        List<CategoryResponseDto> response=new ArrayList<>();
        for(Category category: categories){
            response.add(toResponseDto(category));
        }
        return response;
    }

    public CategoryResponseDto getCategoryById(Long id){
        Category category=categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found with id: "+id));

        return toResponseDto(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request){
        Category category=categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category not found with id: "+id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updateCategory=categoryRepository.save(category);

        return toResponseDto(updateCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id){
        Category category=categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found by id: "+id));

        categoryRepository.delete(category);
    }

    private CategoryResponseDto toResponseDto(Category category) {

        CategoryResponseDto response = new CategoryResponseDto();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        return response;
    }

    private Category toEntity(CategoryRequestDto request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return category;
    }
}
