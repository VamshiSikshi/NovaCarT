package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();
    }

    private Category createCategory(
            String name,
            String description) {

        Category category = new Category();

        category.setName(name);
        category.setDescription(description);

        return categoryRepository.save(category);
    }

    @Test
    void saveCategoryPersistsCategory() {

        Category category =
                createCategory(
                        "Electronics",
                        "Electronic products"
                );

        assertThat(category.getId())
                .isNotNull();

        assertThat(category.getName())
                .isEqualTo("Electronics");

        assertThat(category.getDescription())
                .isEqualTo("Electronic products");
    }

    @Test
    void findByIdReturnsCategory() {

        Category savedCategory =
                createCategory(
                        "Books",
                        "Books and literature"
                );

        Optional<Category> result =
                categoryRepository.findById(
                        savedCategory.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getName())
                .isEqualTo("Books");

        assertThat(result.get().getDescription())
                .isEqualTo("Books and literature");
    }

    @Test
    void findByIdWhenCategoryDoesNotExistReturnsEmpty() {

        Optional<Category> result =
                categoryRepository.findById(999999L);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findAllReturnsAllCategories() {

        createCategory(
                "Electronics",
                "Electronic products"
        );

        createCategory(
                "Books",
                "Books and literature"
        );

        createCategory(
                "Clothing",
                "Clothing products"
        );

        List<Category> categories =
                categoryRepository.findAll();

        assertThat(categories)
                .hasSize(3);

        assertThat(categories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder(
                        "Electronics",
                        "Books",
                        "Clothing"
                );
    }

    @Test
    void countReturnsNumberOfCategories() {

        createCategory(
                "Electronics",
                "Electronic products"
        );

        createCategory(
                "Books",
                "Books and literature"
        );

        assertThat(categoryRepository.count())
                .isEqualTo(2);
    }

    @Test
    void existsByIdReturnsTrueForExistingCategory() {

        Category savedCategory =
                createCategory(
                        "Electronics",
                        "Electronic products"
                );

        assertThat(
                categoryRepository.existsById(
                        savedCategory.getId()
                )
        ).isTrue();
    }

    @Test
    void existsByIdReturnsFalseForMissingCategory() {

        assertThat(
                categoryRepository.existsById(999999L)
        ).isFalse();
    }

    @Test
    void deleteCategoryRemovesCategory() {

        Category savedCategory =
                createCategory(
                        "Electronics",
                        "Electronic products"
                );

        categoryRepository.delete(savedCategory);

        assertThat(
                categoryRepository.findById(
                        savedCategory.getId()
                )
        ).isEmpty();
    }

    @Test
    void deleteByIdRemovesCategory() {

        Category savedCategory =
                createCategory(
                        "Books",
                        "Books and literature"
                );

        categoryRepository.deleteById(
                savedCategory.getId()
        );

        assertThat(
                categoryRepository.existsById(
                        savedCategory.getId()
                )
        ).isFalse();
    }

    @Test
    void saveCategoryUpdatesExistingCategory() {

        Category category =
                createCategory(
                        "Old Name",
                        "Old description"
                );

        category.setName("New Name");
        category.setDescription("New description");

        Category updatedCategory =
                categoryRepository.save(category);

        assertThat(updatedCategory.getId())
                .isEqualTo(category.getId());

        assertThat(updatedCategory.getName())
                .isEqualTo("New Name");

        assertThat(updatedCategory.getDescription())
                .isEqualTo("New description");

        Category foundCategory =
                categoryRepository
                        .findById(category.getId())
                        .orElseThrow();

        assertThat(foundCategory.getName())
                .isEqualTo("New Name");

        assertThat(foundCategory.getDescription())
                .isEqualTo("New description");
    }
}