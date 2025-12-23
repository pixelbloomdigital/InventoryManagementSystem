package com.pixelbloom.products.controller;

import com.pixelbloom.products.enums.ProductStatus;
import com.pixelbloom.products.model.Product;
import com.pixelbloom.products.response.ProductComparisonResponse;
import com.pixelbloom.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping("/add")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(service.create(product));
    }

    @GetMapping("/getByProductId/{productId}")
    public Product get(@PathVariable Long productId) {
        return service.getById(productId);
    }

    @GetMapping("/getBySubcategoryId")
    public List<Product> getBySubCategoryId(@RequestParam(value = "subCategoryId") Long subCategoryId) {
        return service.getBySubCategoryId(subCategoryId);
    }

    @GetMapping("/getBySubCategoryIdandStatus")
    public List<Product> getBySubCategoryIdandStatus(@RequestParam(value="subCategoryId",required= false)Long subCategoryId, @RequestParam(value = "status") ProductStatus status) {
        return service.getBySubcategoryAndStatus(subCategoryId,status);
    }

    @GetMapping("/compareProduct")
    public List<Product> getProducts(
            @RequestParam Long subcategoryId,
            @RequestParam List<Long> productIds) {

        return service.getProducts(subcategoryId, productIds);

    }


    @PostMapping("/checkSellable")
    public ResponseEntity<?> checkSellable(@RequestBody List<Product> product) {
        return ResponseEntity.ok(service.getAvailableProducts(product));
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product);
    }

    @PatchMapping("/{id}/disable")
    public void disable(@PathVariable Long id) {
        service.disable(id);
    }

    @PatchMapping("/{id}/enable")
    public void enable(@PathVariable Long id) {
        service.enable(id);
    }



}
