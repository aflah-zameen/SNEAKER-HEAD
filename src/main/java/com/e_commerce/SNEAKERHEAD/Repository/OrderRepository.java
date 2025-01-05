package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    List<OrderEntity> findAllByUser_id(Long userId);
    List<OrderEntity> findAllByStatus(String status);
    Page<OrderEntity> findAllByStatus(String status,Pageable pageable);

    @Query(value = "SELECT o FROM OrderEntity o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND (:startDate IS NULL OR o.orderDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.orderDate <= :endDate)")
    Page<OrderEntity> findAllDeliveredOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageablePage);

    Page<OrderEntity> findByOrderDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, Pageable pageablePage, String delivered);
    List<OrderEntity> findByOrderDateBetweenAndStatus(LocalDate startDate, LocalDate endDate,String delivered);
}
