//package com.e_commerce.SNEAKERHEAD.Repository;
//
//import com.e_commerce.SNEAKERHEAD.Entity.OrderEntity;
//import com.e_commerce.SNEAKERHEAD.Entity.Product;
//import jakarta.persistence.Access;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Predicate;
//import jakarta.persistence.criteria.Root;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class CriticalOrderRepository {
//
//    @Autowired
//    private EntityManager em;
//
//    public Page<Order> findAllByDelivered(LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        // Create CriteriaBuilder
//        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
//
//        // Create CriteriaQuery
//        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
//
//        // Define the root (FROM clause)
//        Root<Order> root = criteriaQuery.from(Order.class);
//
//        // Create predicates (WHERE clause conditions)
//        Predicate deliveredStatus = criteriaBuilder.equal(root.get("status"), "DELIVERED");
//
//        Predicate datePredicate = criteriaBuilder.conjunction(); // Start with no condition
//        if (startDate != null) {
//            datePredicate = criteriaBuilder.and(datePredicate, criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), startDate));
//        }
//        if (endDate != null) {
//            datePredicate = criteriaBuilder.and(datePredicate, criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), endDate));
//        }
//
//        // Combine all predicates
//        Predicate finalPredicate = criteriaBuilder.and(deliveredStatus, datePredicate);
//
//        // Apply the predicates to the query
//        criteriaQuery.where(finalPredicate);
//
//        // Apply sorting (ORDER BY)
//        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
//
//        // Create the query and apply pagination
//        TypedQuery<Order> query = em.createQuery(criteriaQuery);
//
//        // Set pagination parameters
//        query.setFirstResult((int) pageable.getOffset());
//        query.setMaxResults(pageable.getPageSize());
//
//        // Execute query and fetch results
//        List<Order> resultList = query.getResultList();
//
//        // Fetch total count for pagination metadata
//        long totalCount = getTotalCount(finalPredicate);
//
//        // Return a Page object
//        return new PageImpl<>(resultList, pageable, totalCount);
//    }
//
//    private long getTotalCount(Predicate predicate) {
//        // Create a CriteriaQuery for count
//        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
//        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//        Root<Order> root = countQuery.from(Order.class);
//
//        // Apply the same predicates
//        countQuery.select(criteriaBuilder.count(root)).where(predicate);
//
//        // Execute the count query
//        return em.createQuery(countQuery).getSingleResult();
//    }
//}
