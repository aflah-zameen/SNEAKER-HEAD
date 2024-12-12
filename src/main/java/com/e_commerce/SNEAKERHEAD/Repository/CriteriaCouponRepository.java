package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.Brand;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Coupon;
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
public class CriteriaCouponRepository {
    @Autowired
    private final EntityManager em;

    public List<Coupon> searchAllCoupons(String keyword)
    {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Coupon> criteriaQuery = criteriaBuilder.createQuery(Coupon.class);

        // Join the 'category' table

        //select * from product
        Root<Coupon> root = criteriaQuery.from(Coupon.class);

        //prepare WHERE clause
        //Where firstname like "%ali%"
//        Predicate product_id = criteriaBuilder
//                .equal(root.get("id"),Integer.parseInt(keyword));

        Predicate couponName = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("couponName")),"%"+keyword.toLowerCase()+"%");

        Predicate couponCodePredicate = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("couponCode")),"%"+keyword.toLowerCase()+"%");

        Predicate orPredicate = criteriaBuilder.or(
           couponName,couponCodePredicate
    );
        criteriaQuery.where(orPredicate);
        TypedQuery<Coupon> query =em.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
