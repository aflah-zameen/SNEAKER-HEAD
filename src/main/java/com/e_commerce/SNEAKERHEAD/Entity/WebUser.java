package com.e_commerce.SNEAKERHEAD.Entity;


import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import com.e_commerce.SNEAKERHEAD.Enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@Table(name = "user_table")
public class WebUser{
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private Long phone;

    @Column(name = "password")
    private String password;

    @Column(name = "is_blocked")
    private Boolean is_blocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "join_date")
    private LocalDate join_date;

    @Column(name="gender")
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserAddress> addresses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Cart> carts;


}