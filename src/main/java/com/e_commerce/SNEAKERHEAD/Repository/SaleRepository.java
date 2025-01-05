package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleRepository extends JpaRepository<OrderItems,Long> {
    @Query("SELECT p.name AS productName, SUM(oi.quantity) AS totalSales " +
            "FROM OrderItems oi JOIN oi.productVariant.product p " +
            "GROUP BY p.name ORDER BY totalSales DESC")
    List<Object[]> getTopSellingProducts();

    @Query("SELECT c.name AS categoryName, SUM(oi.quantity) AS totalSales " +
            "FROM OrderItems oi JOIN oi.productVariant.product p JOIN p.category c " +
            "GROUP BY c.name ORDER BY totalSales DESC")
    List<Object[]> getTopSellingCategories();

    @Query("SELECT b.name AS brandName, SUM(oi.quantity) AS totalSales " +
            "FROM OrderItems oi JOIN oi.productVariant.product p JOIN p.brand b " +
            "GROUP BY b.name ORDER BY totalSales DESC")
    List<Object[]> getTopSellingBrands();
}
