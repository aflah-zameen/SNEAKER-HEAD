package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.Coupon;
import com.e_commerce.SNEAKERHEAD.Entity.Order;
import com.e_commerce.SNEAKERHEAD.Entity.UserCouponUsage;
import com.e_commerce.SNEAKERHEAD.Entity.WebUser;
import com.e_commerce.SNEAKERHEAD.Repository.CouponRepository;
import com.e_commerce.SNEAKERHEAD.Repository.OrderRepository;
import com.e_commerce.SNEAKERHEAD.Repository.UserCouponUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserCouponUsageRepository userCouponUsageRepository;

    public Coupon addCoupon(Coupon couponObject)
    {
        couponObject.setIsActive(true);
        couponObject.setUsedCount(0);
        couponObject.setCreateAt(LocalDate.now());
        return couponRepository.save(couponObject);
    }

    public List<Coupon> ListCoupons(WebUser user) {

    List<Coupon> coupons = couponRepository.findAllByIsActive(true);
    List<Order> orders = orderRepository.findAllByUser_id(user.getId());
    coupons = coupons.stream()
            .filter(cp-> !cp.getNewUserCoupon() || orderRepository.findAllByUser_id(user.getId()).isEmpty()).collect(Collectors.toList());
        System.out.println(coupons);
    coupons =coupons.stream()
            .filter(cp-> !cp.getNewUserCoupon() || orderRepository.findAllByUser_id(user.getId()).isEmpty())
            .filter(cp->(cp.getStartDate().isBefore(LocalDate.now()) || cp.getStartDate().isEqual(LocalDate.now()))&&(cp.getEndDate().isAfter(LocalDate.now()) || cp.getEndDate().isEqual(LocalDate.now())))
            .filter(cp-> cp.getUsageLimit() > userCouponUsageRepository.findByUser_idAndCoupon_id(user.getId(), cp.getId()).orElse(new UserCouponUsage(0)).getUsageCount())
            .collect(Collectors.toList());
    return coupons;
    }
}
