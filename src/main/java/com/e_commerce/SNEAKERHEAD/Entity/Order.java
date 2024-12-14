package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "order_details")
public class Order {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",nullable = false)
    private WebUser user;

    @OneToOne
    @JoinColumn(name = "coupon_id",nullable = false)
    private Coupon coupon;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private UserAddress address;

    @Column(name = "delivery_charge")
    private Double deliveryCharge;

    @Column(name="order_date")
    private LocalDate orderDate;

    @Column(name = "order_total_amount")
    private Double orderTotal;

    @Column(name = "tax")
    private String tax;

    @Column(name="status")
    private String status;

    @Column(name="payment_method")
    private String paymentMethod;

    @Column(name = "cancellation")
    private boolean cancellation;

    @OneToMany(mappedBy ="order",cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;

    public String getFormattedPrice(){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(orderTotal);
    }
}
