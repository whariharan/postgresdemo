package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for Product-related operations.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductService(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    // Method to save a product
    public Product saveProduct(ProductRequest productRequest) throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        JsonNode jsonNode = objectMapper.readTree(productRequest.getAttributes().traverse()); // Parse JSON attributes
        product.setAttributes(jsonNode);
        return productRepository.save(product); // Save product to the database
    }

    // Method to get a product by ID
    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id); // Retrieve product from the database
    }

    // Method to get a product by attributes
    public List<Product> getByAttributes(Map<String, List<String>> filterMap) throws IOException {

        JsonNode headerJsonNode = readJsonFile("C:\\poc\\demo\\src\\main\\resources\\test.json");
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = filterMap.entrySet().stream()
            .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty()) // Filter out empty values
            .map(entry -> {
                Expression<String> expression = cb.function("jsonb_extract_path_text", String.class, product.get("attributes"), cb.literal(entry.getKey()));
                return expression.in(entry.getValue());
            }
            ).toList();
            query.select(product).where(cb.and(predicates.toArray(new Predicate[0])));

        List<Product> products = entityManager.createQuery(query).getResultList();
        products.forEach(product1 -> {
            JsonNode attributes = product1.getAttributes();
            findKeysNotInFirstJsonNode(attributes, headerJsonNode);
        });

        return products;
    }

    // Method to update a product
    public Product updateProduct(Long id, ProductRequest productRequest) throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productRequest.getName());
            JsonNode jsonNode = objectMapper.readTree(productRequest.getAttributes().traverse()); // Parse JSON attributes
            product.setAttributes(jsonNode);
            return productRepository.save(product); // Save updated product to the database
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    // Method to delete a product
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id); // Delete product from the database
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    public void findKeysNotInFirstJsonNode(JsonNode dbJsonNode, JsonNode headerJsonNode) {
        Iterator<JsonNode> iterator = headerJsonNode.elements();
        while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next();
                if (jsonNode.has("name") && !dbJsonNode.has(jsonNode.get("name").asText())) {
                        dbJsonNode = ((ObjectNode) dbJsonNode).put(jsonNode.get("name").asText(), "");
                }
        }
    }
     // Method to read a JSON file and map to JsonNode
    public JsonNode readJsonFile(String filePath) throws IOException {
        File file = new File(filePath);
        return objectMapper.readTree(file);
    }
}