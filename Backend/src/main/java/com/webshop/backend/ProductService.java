package com.webshop.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryManagement inventoryManagement;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }


    public Product updateProduct(Long productId, Product updatedProduct) {
        return productRepository.findById(productId).map(product -> {
            product.setProductName(updatedProduct.getProductName());
            product.setDescription(updatedProduct.getDescription());
            product.setBasePrice(updatedProduct.getBasePrice());
            product.setCategoryId(updatedProduct.getCategoryId());
            product.setWeightKg(updatedProduct.getWeightKg());
            product.setDimensionsCm(updatedProduct.getDimensionsCm());
            product.setMaterialType(updatedProduct.getMaterialType());
            product.setImageUrl(updatedProduct.getImageUrl());
            product.setSupplierName(updatedProduct.getSupplierName());
            product.setSupplierEmail(updatedProduct.getSupplierEmail());
            product.setSupplierPhone(updatedProduct.getSupplierPhone());
            product.setStockQuantity(updatedProduct.getStockQuantity());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public List<Product> searchProductsByName(String productName) {
        return productRepository.findByProductNameContainingIgnoreCase(productName);

    }
}

