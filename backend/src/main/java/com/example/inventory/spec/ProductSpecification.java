package com.example.inventory.spec;

import com.example.inventory.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    // MAIN SEARCH → name, sku, category, brand
    public static Specification<Product> search(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;

            String like = "%" + q.toLowerCase().trim() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("sku")), like),
                    cb.like(cb.lower(root.get("category")), like),
                    cb.like(cb.lower(root.get("brand")), like)
            );
        };
    }

    // PRICE RANGE FILTER
    public static Specification<Product> priceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {

            if (minPrice == null && maxPrice == null) return null;

            if (minPrice == null)
                return cb.le(root.get("price"), maxPrice);

            if (maxPrice == null)
                return cb.ge(root.get("price"), minPrice);

            return cb.and(
                    cb.ge(root.get("price"), minPrice),
                    cb.le(root.get("price"), maxPrice)
            );
        };
    }

    // STOCK RANGE FILTER
    public static Specification<Product> stockBetween(Integer minStock, Integer maxStock) {
        return (root, query, cb) -> {

            if (minStock == null && maxStock == null) return null;

            if (minStock == null)
                return cb.le(root.get("stock"), maxStock);

            if (maxStock == null)
                return cb.ge(root.get("stock"), minStock);

            return cb.and(
                    cb.ge(root.get("stock"), minStock),
                    cb.le(root.get("stock"), maxStock)
            );
        };
    }

    // CATEGORY FILTER
    public static Specification<Product> categoryEquals(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;

            return cb.equal(
                    cb.lower(root.get("category")),
                    category.toLowerCase().trim()
            );
        };
    }
}




