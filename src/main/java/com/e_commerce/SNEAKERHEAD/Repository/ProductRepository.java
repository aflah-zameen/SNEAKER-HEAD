package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.DTO.ProductDto;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariants pv WHERE p.category = :category")
    List<Product> findByCategory(@Param("category")Category catgeory);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariants pv WHERE p.category = :category ORDER BY pv.price ASC")
    List<Product> findByCategoryAndSortByPriceAsc(@Param("category") Category category);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productVariants pv WHERE p.category = :category ORDER BY pv.price DESC")
    List<Product> findByCategoryAndSortByPriceDesc(@Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name ASC")
    List<Product> findByCategoryAndSortByNameAsc(@Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE p.category = :category ORDER BY p.name DESC")
    List<Product> findByCategoryAndSortByNameDesc(@Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE "+
            "LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR "+
            "LOWER(p.brand.name) LIKE LOWER(CONCAT('%',:keyword,'%'))"
    )
    List<Product> searchProducts(String keyword);

    Page<Product> findAll(Pageable pageable);


    List<Product> findAllByStatus(boolean b);
}
