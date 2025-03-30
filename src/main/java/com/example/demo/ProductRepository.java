package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT p.id, p.attributes,p.name FROM products p WHERE p.attributes @> CAST(:json AS jsonb)", nativeQuery = true)
    List<Product> findByAttributeContains(@Param("json") String json);
}