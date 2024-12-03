package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Setter
@Getter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private WebUser user;

    @Column(name = "quantity",nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_variant_id",nullable = false)
    private ProductVariant productVariant;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name="size")
    private Double size;

    public String getFormattedPrice(){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(totalAmount);
    }

}
