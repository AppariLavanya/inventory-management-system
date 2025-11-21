package com.example.inventory.repository;

import com.example.inventory.model.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    // Low stock list (used by analytics & old low-stock page)
    List<Product> findByStockLessThanEqual(Integer threshold);

    // Low stock PAGED (used for NEW enhancements)
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold")
    Page<Product> findLowStock(@Param("threshold") Integer threshold, Pageable pageable);

    // CATEGORY COUNTS
    @Query("SELECT COALESCE(p.category, 'Unknown') AS category, COUNT(p) FROM Product p GROUP BY COALESCE(p.category, 'Unknown')")
    List<Object[]> getCategoryCounts();

    // PRICE SEGMENTS
    @Query("SELECT " +
            "CASE " +
            " WHEN p.price <= 20000 THEN '0 - 20000' " +
            " WHEN p.price <= 50000 THEN '20000 - 50000' " +
            " WHEN p.price <= 100000 THEN '50000 - 100000' " +
            " ELSE '100000+' END AS range, " +
            " COUNT(p) FROM Product p GROUP BY " +
            " CASE " +
            " WHEN p.price <= 20000 THEN '0 - 20000' " +
            " WHEN p.price <= 50000 THEN '20000 - 50000' " +
            " WHEN p.price <= 100000 THEN '50000 - 100000' " +
            " ELSE '100000+' END")
    List<Object[]> getPriceSegments();

    // BRAND COUNTS
    @Query("SELECT COALESCE(p.brand, 'Unknown') AS brand, COUNT(p) FROM Product p GROUP BY COALESCE(p.brand, 'Unknown')")
    List<Object[]> getBrandCounts();
}







