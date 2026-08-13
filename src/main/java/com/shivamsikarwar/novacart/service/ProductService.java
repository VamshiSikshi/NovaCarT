package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.ProductCategoryDto;
import com.shivamsikarwar.novacart.dto.ProductRequestDto;
import com.shivamsikarwar.novacart.dto.ProductResponseDto;
import com.shivamsikarwar.novacart.dto.ProductSummaryDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.specification.ProductSpecification;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
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

    @Cacheable(value="products", key="#id")
    @Transactional
    public ProductResponseDto getProductById(Long id) {
        Product product=findProductById(id);
        return toResponseDto(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(
            value = "products",
            key="#id"
    )
    @Transactional
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

    @CacheEvict(
            value="products",
            key="#id"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteProduct(Long id) {
        Product product=findProductById(id);
        productRepository.delete(product);
    }

    public List<ProductResponseDto> searchProducts(String keyword){

        return productRepository.searchProducts(keyword)
                .stream().map(this::toResponseDto).toList();
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId){
        findCategoryById(categoryId);
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

    public List<ProductResponseDto> getProductsByPriceDesc(){
        return productRepository.findAllOrderByPriceDesc()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }


    public Long countProducts(){
        return productRepository.countProducts();
    }

    public BigDecimal getHighestPrice(){
        return productRepository.getHighestPrice();
    }

    public BigDecimal getLowestPrice(){
        return productRepository.getLowestPrice();
    }

    public Double getAveragePrice(){
        return productRepository.getAveragePrice();
    }

    public Long getTotalStock(){
        return productRepository.getTotalStock();
    }

    public List<ProductSummaryDto> getProductSummaries(){
        return productRepository.getProductSummaries();
    }

    public List<ProductCategoryDto> getProductsWithCategory(){
        return productRepository.getProductWithCategory();
    }

    public List<ProductResponseDto> getAllProductsWithCategory(){
        return productRepository.findAllWithCategory()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public int increaseAllPrices(){
        return productRepository.increaseAllPrices();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public int deleteOutOfStockProducts(){
        return productRepository.deleteOutOfStockProducts();
    }

    public List<ProductResponseDto> getAvailableProducts(Integer stock){
        return productRepository.findAvailableProducts(stock)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<ProductResponseDto> searchProducts(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        Specification<Product> specification =Specification.unrestricted();

        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(ProductSpecification.hasName(keyword));
        }

        if (categoryId != null) {
            specification = specification.and(ProductSpecification.hasCategory(categoryId));
        }

        if (minPrice != null) {
            specification = specification.and(ProductSpecification.hasMinPrice(minPrice));
        }

        if (maxPrice != null) {
            specification = specification.and(ProductSpecification.hasMaxPrice(maxPrice));
        }

        specification = specification.and(ProductSpecification.inStock());

        return productRepository.findAll(specification)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<ProductResponseDto> getAllProductsWithEntityGraph(){
        return productRepository.findAllBy()
                .stream()
                .map(this::toResponseDto)
                .toList();
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
