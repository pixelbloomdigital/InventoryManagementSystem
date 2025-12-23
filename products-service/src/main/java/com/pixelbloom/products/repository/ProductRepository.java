package com.pixelbloom.products.repository;

import com.pixelbloom.products.enums.ProductStatus;
import com.pixelbloom.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByIdAndStatus(Long id, ProductStatus status);
    List<Product> findBySubcategoryIdAndStatus(Long subcategoryId, ProductStatus status);

    List<Product> findBySubcategoryId(Long subcategoryId);
    List<Product> findBySubcategoryIdAndId(
            Long subcategoryId,
            Long id
    );
    List<Product> findBySubcategoryIdAndIdIn(
            Long subcategoryId,
            List<Long> ids
    );
}