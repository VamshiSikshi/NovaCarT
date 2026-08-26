package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.CategoryRequestDto;
import com.shivamsikarwar.novacart.dto.CategoryResponseDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private Category category2;
    private CategoryRequestDto request;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic products");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");
        category2.setDescription("Clothing products");

        request = new CategoryRequestDto();
        request.setName("Electronics");
        request.setDescription("Electronic products");
    }

    @Test
    void createCategoryCreatesAndReturnsCategory() {

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponseDto response =
                categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals(
                "Electronic products",
                response.getDescription()
        );

        verify(categoryRepository)
                .save(any(Category.class));
    }

    @Test
    void getAllCategoriesReturnsEmptyListWhenNoCategoriesExist() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<CategoryResponseDto> response =
                categoryService.getAllCategories();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(categoryRepository)
                .findAll();
    }

    @Test
    void getAllCategoriesReturnsAllCategories() {

        when(categoryRepository.findAll())
                .thenReturn(List.of(category, category2));

        List<CategoryResponseDto> response =
                categoryService.getAllCategories();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals(
                "Electronics",
                response.get(0).getName()
        );

        assertEquals(2L, response.get(1).getId());
        assertEquals(
                "Clothing",
                response.get(1).getName()
        );

        verify(categoryRepository)
                .findAll();
    }

    @Test
    void getCategoryByIdReturnsCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        CategoryResponseDto response =
                categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "Electronics",
                response.getName()
        );
        assertEquals(
                "Electronic products",
                response.getDescription()
        );

        verify(categoryRepository)
                .findById(1L);
    }

    @Test
    void getCategoryByIdThrowsWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(999L)
        );

        verify(categoryRepository)
                .findById(999L);
    }

    @Test
    void updateCategoryUpdatesAndReturnsCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        CategoryRequestDto updateRequest =
                new CategoryRequestDto();

        updateRequest.setName("Updated Electronics");
        updateRequest.setDescription(
                "Updated electronic products"
        );

        CategoryResponseDto response =
                categoryService.updateCategory(
                        1L,
                        updateRequest
                );

        assertNotNull(response);

        assertEquals(
                "Updated Electronics",
                response.getName()
        );

        assertEquals(
                "Updated electronic products",
                response.getDescription()
        );

        verify(categoryRepository)
                .findById(1L);

        verify(categoryRepository)
                .save(category);
    }

    @Test
    void updateCategoryThrowsWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(
                        999L,
                        request
                )
        );

        verify(categoryRepository)
                .findById(999L);

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void deleteCategoryDeletesExistingCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository)
                .findById(1L);

        verify(categoryRepository)
                .delete(category);
    }

    @Test
    void deleteCategoryThrowsWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(999L)
        );

        verify(categoryRepository)
                .findById(999L);

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
}