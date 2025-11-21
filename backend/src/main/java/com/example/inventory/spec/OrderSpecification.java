package com.example.inventory.spec;

import com.example.inventory.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class OrderSpecification {

    public static Specification<Order> customerNameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            String like = "%" + name.toLowerCase().trim() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("customerName")), like),
                    cb.like(cb.lower(root.get("customerEmail")), like)
            );
        };
    }

    public static Specification<Order> minTotal(Double minTotal) {
        return (root, query, cb) ->
                minTotal == null ? null : cb.ge(root.get("total"), minTotal);
    }

    public static Specification<Order> maxTotal(Double maxTotal) {
        return (root, query, cb) ->
                maxTotal == null ? null : cb.le(root.get("total"), maxTotal);
    }

    public static Specification<Order> createdAfter(Instant from) {
        return (root, query, cb) ->
                from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Order> createdBefore(Instant to) {
        return (root, query, cb) ->
                to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}







