package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ManyToAny;

@Getter
@Setter
@Entity
@Table(name = "user_coupon_usage")
@RequiredArgsConstructor
public class UserCouponUsage {
    public UserCouponUsage(Integer usageCount)
    {
        this.usageCount=usageCount;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id ;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private WebUser user;

    @ManyToOne
    @JoinColumn(name = "coupon_id",nullable = false)
    private Coupon coupon;

    @Column(name = "usage_count")
    private Integer usageCount;

}
