package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.DTO.ProductDto;
import com.e_commerce.SNEAKERHEAD.Entity.Brand;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CriteriaProductRepository {
    @Autowired
    private final EntityManager em;

    public List<Product> searchAllProducts(String keyword)
    {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);

        // Join the 'category' table

        //select * from product
        Root<Product> root = criteriaQuery.from(Product.class);

        Join<Product, Category> categoryJoin = root.join("category");
        Join<Product, Brand> brandJoin = root.join("brand");

        //prepare WHERE clause
        //Where firstname like "%ali%"
//        Predicate product_id = criteriaBuilder
//                .equal(root.get("id"),Integer.parseInt(keyword));

        Predicate productName = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("name")),"%"+keyword.toLowerCase()+"%");

        Predicate categoryPredicate = criteriaBuilder
                .like(criteriaBuilder.lower(categoryJoin.get("name")),"%"+keyword.toLowerCase()+"%");

        Predicate brandPredicate = criteriaBuilder
                .like(criteriaBuilder.lower(brandJoin.get("name")),"%"+keyword.toLowerCase()+"%");

        Predicate orPredicate = criteriaBuilder.or(
           productName,categoryPredicate,brandPredicate
    );
        criteriaQuery.where(orPredicate);
        TypedQuery<Product> query =em.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
