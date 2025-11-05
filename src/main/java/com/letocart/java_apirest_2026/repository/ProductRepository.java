package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Trouver les produits par nom (recherche partielle)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Trouver les produits en stock
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
}
