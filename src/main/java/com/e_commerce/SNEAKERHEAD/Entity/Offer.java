package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name="offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name="category_id")
    private Category category;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name = "description")
    private String description;
}
