package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.ProductCategoryDto;
import com.shivamsikarwar.novacart.dto.ProductRequestDto;
import com.shivamsikarwar.novacart.dto.ProductResponseDto;
import com.shivamsikarwar.novacart.dto.ProductSummaryDto;
import com.shivamsikarwar.novacart.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @PostMapping
    public ProductResponseDto createProduct(@Valid @RequestBody ProductRequestDto request){
        return productService.createProduct(request);
    }

    @GetMapping
    public Page<ProductResponseDto> getAllProducts(
        @PageableDefault(size=10,sort="id", direction= Sort.Direction.DESC) Pageable pageable){
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(
            @PathVariable Long id) {

        return productService.getProductsById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {

        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public List<ProductResponseDto> searchProducts(@RequestParam String keyword){
        return productService.searchProducts(keyword);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> getProductsByCategory(@PathVariable Long categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/price-range")
    public List<ProductResponseDto> filterProductsByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice){
        return productService.filterProductsByPrice(minPrice, maxPrice);
    }

    @GetMapping("/expensive")
    public List<ProductResponseDto> getExpensiveProducts(@RequestParam BigDecimal price){
        return productService.getExpensiveProducts(price);
    }

    @GetMapping("/expensive-first")
    public List<ProductResponseDto> getProductsByPriceDesc(){
        return productService.getProductsByPriceDesc();
    }

    @GetMapping("/count")
    public Long countProducts(){
        return productService.countProducts();
    }

    @GetMapping("/highest-price")
    public BigDecimal getHighestPrice(){
        return productService.getHighestPrice();
    }

    @GetMapping("/lowest-price")
    public BigDecimal getLowestPrice(){
        return productService.getLowestPrice();
    }

    @GetMapping("/average-price")
    public Double getAveragePrice(){
        return productService.getAveragePrice();
    }

    @GetMapping("/total-stock")
    public Long getTotalStock(){
        return productService.getTotalStock();
    }

    @GetMapping("/summary")
    public List<ProductSummaryDto> getProductsSummaries(){
        return productService.getProductSummaries();
    }

    @GetMapping("/with-category")
    public List<ProductCategoryDto> getProductsWithCategory(){
        return productService.getProductsWithCategory();
    }

    @GetMapping("/with-category-fetch")
    public List<ProductResponseDto> getAllProductsWithCategory(){
        return productService.getAllProductsWithCategory();
    }

    @PutMapping("/increase-price")
    public String increaseAllPrices(){
        int updateRows= productService.increaseAllPrices();
        return updateRows+" products updated,";
    }

    @DeleteMapping("/out-of-stock")
    public String deleteOutOfStockProduct(){
        int deleteRows= productService.deleteOutOfStockProducts();
        return deleteRows + " products deleted.";
    }

    @GetMapping("/available")
    public List<ProductResponseDto> getAvailableProducts(@RequestParam Integer stock){
        return productService.getAvailableProducts(stock);
    }

    @GetMapping("/filter")
    public List<ProductResponseDto> filterProducts(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice) {

        return productService.searchProducts(
                keyword,
                categoryId,
                minPrice,
                maxPrice
        );
    }
}
