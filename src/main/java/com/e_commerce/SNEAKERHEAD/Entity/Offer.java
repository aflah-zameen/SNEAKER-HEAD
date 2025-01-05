package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
    private Integer discountValue;

    @Column(name="type_id")
    private Long typeId;

    @Column(name ="type")
    private String type;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name = "description")
    private String description;

    @Column(name="offer_name")
    private String offerName;

    @Column(name="end_date")
    private LocalDate endDate;
}
