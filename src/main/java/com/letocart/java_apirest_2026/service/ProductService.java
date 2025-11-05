package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.model.Product;
import com.letocart.java_apirest_2026.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsInStock() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    public Product updateProduct(Long id, Product productDetails) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Produit non trouvé avec l'ID: " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) throws Exception {
        if (!productRepository.existsById(id)) {
            throw new Exception("Produit non trouvé avec l'ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
