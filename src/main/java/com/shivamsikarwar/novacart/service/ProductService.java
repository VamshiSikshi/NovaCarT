package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.ProductRequestDto;
import com.shivamsikarwar.novacart.dto.ProductResponseDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository){
        this.productRepository=productRepository;
        this.categoryRepository=categoryRepository;
    }

    public ProductResponseDto createProduct(ProductRequestDto request){
        Category category=findCategoryById(request.getCategoryId());
        Product product=toEntity(request, category);
        Product savedProduct=productRepository.save(product);
        return toResponseDto(savedProduct);
    }

    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::toResponseDto);
    }

    public ProductResponseDto getProductById(Long id) {
        Product product=findProductById(id);
        return toResponseDto(product);
    }

    public ProductResponseDto updateProduct(Long id, ProductRequestDto request) {
        Product product=findProductById(id);
        Category category=findCategoryById(request.getCategoryId());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return toResponseDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product=findProductById(id);
        productRepository.delete(product);
    }

    public List<ProductResponseDto> searchProducts(String keyword){

        return productRepository.searchProducts(keyword)
                .stream().map(this::toResponseDto).toList();
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId){
        Category category=findCategoryById(categoryId);
        return productRepository.findByCategoryId(categoryId).stream().map(this::toResponseDto).toList();
    }

    public List<ProductResponseDto> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice){
        return productRepository.findByPriceBetween(minPrice,maxPrice).stream().map(this::toResponseDto).toList();
    }

    public List<ProductResponseDto> getExpensiveProducts(BigDecimal price){
        return productRepository
                .findProductsWithPriceGreaterThan(price)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));
    }

    private ProductResponseDto toResponseDto(Product product){
        ProductResponseDto response=new ProductResponseDto();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setImageUrl(product.getImageUrl());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        return response;
    }

    private Product toEntity(ProductRequestDto request, Category category) {
        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return product;
    }

}
