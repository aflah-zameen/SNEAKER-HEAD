package com.e_commerce.SNEAKERHEAD.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "address")
public class UserAddress {
    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private WebUser user;

    @Column(name = "street")
    private String street;

    @Column(name = "building")
    private String building;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private Long phone;

    @Column(name = "country")
    private String country;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "default_address")
    private Boolean defaultAddressStatus;

    @Column(name = "type")
    private String type;

    @Column(name= "status")
    private String status;

    @OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
    private List<OrderEntity> orders;
}
