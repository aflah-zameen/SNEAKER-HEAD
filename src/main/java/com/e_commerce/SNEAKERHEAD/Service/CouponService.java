package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.Coupon;
import com.e_commerce.SNEAKERHEAD.Repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CouponService {

    @Autowired
    CouponRepository couponRepository;



    public Coupon addCoupon(Coupon couponObject)
    {
        couponObject.setIsActive(true);
        couponObject.setUsedCount(0);
        couponObject.setCreateAt(LocalDate.now());
        return couponRepository.save(couponObject);
    }
}
