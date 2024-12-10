package com.e_commerce.SNEAKERHEAD.Entity;


import com.e_commerce.SNEAKERHEAD.Enums.UserRole;
import com.e_commerce.SNEAKERHEAD.Enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "full_name")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Phone number is required")
    @Column(name = "phone")
    private Long phone;

    @NotBlank(message="Password is required")
    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Boolean status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name="gender")
    private String gender;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserAddress> addresses;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Cart> carts;


}