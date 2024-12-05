package com.e_commerce.SNEAKERHEAD.Entity;

import com.e_commerce.SNEAKERHEAD.Enums.BrandStatus;
import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "brand")
public class Brand {
    @Id
    @Column(name = "brand_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "brand_status")
    private BrandStatus brandStatus;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Product> products;
}
