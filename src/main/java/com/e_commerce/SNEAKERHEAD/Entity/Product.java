package com.e_commerce.SNEAKERHEAD.Entity;

import com.e_commerce.SNEAKERHEAD.Enums.StockStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
        @ToString
@Entity
@Table(name = "product")
public class Product{

        @Id
        @Column(name = "product_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private Category category;

        @ManyToOne
        @JoinColumn(name = "brand_id",nullable = false)
        private Brand brand;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ProductVariant> productVariants;

        @Column(name = "status")
        private Boolean status;

        @Column(name = "description")
        private String description;

        @Column(name = "generic_name")
        private String genericName;

        @Column(name = "weight")
        private Double weight;

        @Column(name = "manufacturer")
        private String manufacturer;

        @Column(name = "country_of_origin")
        private String countryOfOrigin;

        @Column(name = "imported_by")
        private String importedBy;

        @Column(name = "marketed_by")
        private String marketedBy;

        @Column(name = "name")
        private String name;

    }


