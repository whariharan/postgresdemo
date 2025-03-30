package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller class for managing Product endpoints.
 */
@RestController
@RequestMapping("/products") // Base URL for product endpoints
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST request to create a new product
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductRequest productRequest) {
        try {
            productService.saveProduct(productRequest); // Save product
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully"); // Return response
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create product"); // Handle errors
        }
    }

    // GET request to retrieve a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
                .map(ResponseEntity::ok) // Return product if found
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // GET request to retrieve a product by Attributes
    @PostMapping("/getProductByAttributes")
    public ResponseEntity<List<Product>> getProductByAttributes(@RequestBody Map<String, List<String>> filterMap) throws IOException {
        List<Product> products = productService.getByAttributes(filterMap);
        return ResponseEntity.ok(products); // Return products
    }

    // PUT request to update a product
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        try {
            productService.updateProduct(id, productRequest); // Update product
            return ResponseEntity.ok("Product updated successfully"); // Return response
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Handle product not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product"); // Handle other errors
        }
    }

    // DELETE request to delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id); // Delete product
            return ResponseEntity.ok("Product deleted successfully"); // Return response
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Handle product not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete product"); // Handle other errors
        }
    }

    @GetMapping("/getMethodName")
    public String getMethodName() {
        return new String("Am there");
    }
    
}