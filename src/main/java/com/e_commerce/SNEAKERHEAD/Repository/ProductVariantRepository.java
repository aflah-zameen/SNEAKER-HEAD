package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {
    Optional<List<ProductVariant>> findByProduct_id(Long product_id);

    @Query("SELECT SUM(v.quantity) FROM ProductVariant v WHERE v.product.id = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") Long productId);

    boolean existsByArticleCode(String color);

}
