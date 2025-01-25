package com.webshop.backend.product;
import com.webshop.backend.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
}
