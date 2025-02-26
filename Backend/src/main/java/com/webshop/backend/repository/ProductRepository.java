package com.webshop.backend.repository;
import com.webshop.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    List<Product> findByCategoryId(Integer categoryId);
    //List<Product> findByPriceBetweenAndCategory(Double minPrice, Double maxPrice, String category);
}
