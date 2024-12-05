package com.e_commerce.SNEAKERHEAD.Entity;

import com.e_commerce.SNEAKERHEAD.Enums.CategoryStatus;
import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Product> products;
}
