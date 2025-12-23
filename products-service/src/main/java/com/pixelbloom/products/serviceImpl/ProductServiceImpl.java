package com.pixelbloom.products.serviceImpl;

import com.pixelbloom.products.controller.ProductController;
import com.pixelbloom.products.enums.ProductStatus;
import com.pixelbloom.products.exception.ResourceNotFoundException;
import com.pixelbloom.products.model.*;
import com.pixelbloom.products.repository.ProductAttributeRepository;
import com.pixelbloom.products.repository.ProductRepository;
import com.pixelbloom.products.response.ProductComparisonResponse;
import com.pixelbloom.products.restcomm.InventoryClient;
import com.pixelbloom.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import com.pixelbloom.products.response.ProductListingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
   private final InventoryClient inventoryClient;

    private final ProductAttributeRepository attributeRepository;

    public ProductServiceImpl(ProductRepository productRepository, InventoryClient inventoryClient,
                              ProductAttributeRepository attributeRepository) {
        this.productRepository = productRepository;
        this.inventoryClient = inventoryClient;
        this.attributeRepository = attributeRepository;
    }

    @Override
    public Product create(Product product) {
        product.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(product);
    }

    @Override
    public Product update(Long id, Product updated) {
        Product existingProduct = getById(id);
        existingProduct.setName(updated.getName());
        existingProduct.setCategoryId(updated.getCategoryId());
        existingProduct.setSubcategoryId(updated.getSubcategoryId());
        return productRepository.save(existingProduct);
    }

    @Override
    public List<Product> getProducts(Long subcategoryId, List<Long> productIds) {
        return productRepository
                .findBySubcategoryIdAndIdIn(subcategoryId, productIds);
    }
    @Override
    public Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getBySubCategoryId(Long subCategoryId) {
        return productRepository.findBySubcategoryId(subCategoryId);
    }

    @Override
    public List<Product> getBySubcategoryAndStatus(Long subcategoryId, ProductStatus status) {
        return productRepository.findBySubcategoryIdAndStatus(subcategoryId, status);
    }

     @Override
    public void disable(Long id) {
        Product product = getById(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }
    @Override
    public void enable(Long id) {
        Product product = getById(id);
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
    }

   public List<ProductListingResponse> getAvailableProducts(List<Product> products) {

        SellCheckRequest request = new SellCheckRequest();
        request.setItems(products.stream()
                .map(p -> new SellCheckRequest.SellItem(
                        p.getId(),
                        p.getCategoryId(),
                        p.getSubcategoryId(),
                        1 // for listing, check at least 1 unit
                ))
                .toList());


       SellCheckResponse response = inventoryClient.checkSellable(request);

        if (response == null) {
            throw new RuntimeException("Inventory service is not available");
        }
        return products.stream()
                .filter(p -> response.getResults().stream()
                        .anyMatch(r -> r.getProductId().equals(p.getId()) && r.isSellable()))
                .map(p ->{
                    int quantity = response.getResults().stream()
                            .filter(r -> r.getProductId().equals(p.getId()))
                            .findFirst()
                            .map(SellCheckResponse.SellItemResult::getAvailableQuantity)
                            .orElse(0);
                return  new ProductListingResponse(
                        p.getId(),
                        p.getName(),
                        "IN_STOCK",
                        quantity);
   })
                .toList();
    }
}
