package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.UserCouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponUsageRepository extends JpaRepository<UserCouponUsage,Long> {
    Optional<UserCouponUsage> findByUser_idAndCoupon_id(Long userId, Long couponId);

    List<UserCouponUsage> findAllByUser_id(Long id);
}
