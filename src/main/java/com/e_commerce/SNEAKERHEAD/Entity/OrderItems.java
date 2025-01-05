package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class OrderItems {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="price")
    private Double price;

}
