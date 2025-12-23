package com.pixelbloom.products.service;

import com.pixelbloom.products.enums.ProductStatus;
import com.pixelbloom.products.model.Product;
import com.pixelbloom.products.response.ProductComparisonResponse;
import com.pixelbloom.products.response.ProductListingResponse;

import java.util.List;

public interface ProductService {
    Product create(Product product);
   Product update(Long id, Product product);
    Product getById(Long productId);

    List<Product> getBySubCategoryId(Long subCategoryId);


    List<Product> getBySubcategoryAndStatus(Long subcategoryId, ProductStatus status);
   List<ProductListingResponse> getAvailableProducts(List<Product> products);
  List<Product> getProducts(
          Long subcategoryId,
          List<Long> productIds
  );
    void disable(Long id);
    void enable(Long id);

    //Object getProducts(Long subcategoryId, List<Long> productIds);
}
