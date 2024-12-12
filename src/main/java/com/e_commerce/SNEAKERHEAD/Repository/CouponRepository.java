package com.e_commerce.SNEAKERHEAD.Repository;

import com.e_commerce.SNEAKERHEAD.Entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon,Long> {

    List<Coupon> findAllByIsActive(boolean b);
}
