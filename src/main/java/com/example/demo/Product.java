package com.example.demo;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Product.
 */
@Data
@Entity
@Table(name = "products")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown JSON properties
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    private String name; // Name of the product

    @Column(columnDefinition = "jsonb") // Specify JSONB column type
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode attributes; // Attributes stored as JSON
}
