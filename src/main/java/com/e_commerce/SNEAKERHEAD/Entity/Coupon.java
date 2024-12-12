package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@ToString
@Table(name = "coupon")

public class Coupon {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coupon name is required")
    @Column(name = "coupon_name")
    private String couponName;

    @NotBlank(message = "Coupon code cannot be blank")
    @Column(name = "coupon_code")
    private String couponCode;

    @NotNull(message = "Please enter the discount value")
    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "is_active")
    private Boolean isActive;

    @NotBlank(message = "Please add description")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Please enter the usage limit per user")
    @Column(name="usage_limit")
    private Integer usageLimit;

    @NotNull(message = "Select the start date")
    @Column(name="start_date")
    private LocalDate startDate;

    @NotNull(message = "Select the end date")
    @Column(name="end_date")
    private LocalDate endDate;

    @Column(name = "create_at")
    private LocalDate createAt;

    @NotNull(message = "Please mention the minimum order value")
    @Column(name = "minimum_order_value")
    private Integer minimumOrderValue;

    @Column(name="used_count")
    private Integer usedCount;

    @NotNull(message = "Please mention the discount type")
    @NotBlank(message = "Please mention the discount type" )
    @Column(name="discount_type")
    private String discountType;


}
