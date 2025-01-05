package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "order_details")
public class OrderEntity {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private WebUser user;

    @OneToOne(optional = true)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private UserAddress address;

    @Column(name = "delivery_charge")
    private Double deliveryCharge;

    @Column(name="order_date",nullable = false)
    private LocalDate orderDate;

    @Column(name = "order_total_amount")
    private Double orderTotal;

    @Column(name = "tax")
    private String tax;

    @Column(name="status",nullable = false)
    private String status;

    @Column(name="payment_method")
    private String paymentMethod;

    @Column(name = "cancellation")
    private boolean cancellation;

    @Column(name="return_reason")
    private String returnReason;

    @Column(name="deducted_amount")
    private Double deductedAmount;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="referral_id")
    private ReferralEntity referralEntity;

    @OneToMany(mappedBy ="order",cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;

    public String getFormattedPrice(){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(orderTotal);
    }
}
